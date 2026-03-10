import asyncio
from app.models.chunk_replication import ChunkReplication
from app.services.plan_replication import plan_replication
from app.core.constants import SERVER_BASE_URL
# MUST include http:// or the phone's request will fail
# SERVER_IP = "http://10.70.222.130:8000"  

async def distribute_chunk(db, chunk, manager):
    # 1. Plan the replication (This adds rows to ChunkReplication)
    plan_replication(db, chunk.chunk_id, chunk.chunk_size)
    
    # 2. Re-fetch from DB to ensure we have the newly created rows
    # We filter by 'REPLICATING' to target only the new ones
    new_assignments = db.query(ChunkReplication).filter(
        ChunkReplication.chunk_id == chunk.chunk_id,
        ChunkReplication.replica_status == "REPLICATING"
    ).all()

    if not new_assignments:
        print(f"No devices available to replicate chunk {chunk.chunk_id}")
        return

    # 3. Create a list of tasks to send commands in parallel
    tasks = []
    has_failed_assignments = False

    for assignment in new_assignments:
        if assignment.device_id in manager.active:
            tasks.append(
                manager.send_command(
                    device_id=assignment.device_id,
                    command_type="DOWNLOAD_CHUNK",
                    data={
                        "chunk_id": chunk.chunk_id,
                        "download_url": f"{SERVER_BASE_URL}/chunks/{chunk.chunk_id}/download",
                        "expected_hash": chunk.chunk_hash
                    }
                )
            )
            print(f"SENDING DOWNLOAD CHUNK OVER WS to {assignment.device_id}")
        else:
            print(f"Skipping WS send: Device {assignment.device_id} is not connected. Marking assignment as FAILED.")
            assignment.replica_status = "FAILED"
            has_failed_assignments = True

    if has_failed_assignments:
        db.commit()

    # Send all commands at once instead of one-by-one
    if tasks:
        await asyncio.gather(*tasks)