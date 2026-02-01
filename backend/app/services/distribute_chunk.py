import asyncio
from app.models.chunk_replication import ChunkReplication
from app.services.plan_replication import plan_replication

SERVER_IP = "your-server-ip"  

async def distribute_chunk(db, chunk, manager):
    # 1. Use your function to create the "REPLICATING" entries in DB
    plan_replication(db, chunk.chunk_id, chunk.chunk_size)

    # 2. Find the rows we just created so we know which WebSockets to message
    new_assignments = db.query(ChunkReplication).filter(
        ChunkReplication.chunk_id == chunk.chunk_id,
        ChunkReplication.replica_status == "REPLICATING"
    ).all()

    for assignment in new_assignments:
        # 3. Send the WebSocket Command
        # This tells the phone: "Download this chunk from me"
        await manager.send_command(
            device_id=assignment.device_id,
            command_type="DOWNLOAD_CHUNK",
            data={
                "chunk_id": chunk.chunk_id,
                "download_url": f"{SERVER_IP}/chunks/download/{chunk.chunk_id}",
                "expected_hash": chunk.chunk_hash
            }
        )