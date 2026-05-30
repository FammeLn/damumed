import asyncio
import httpx
import logging
import sys

logging.basicConfig(level=logging.INFO, stream=sys.stdout)

async def test():
    client = httpx.AsyncClient(
        headers={
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "accept": "text/event-stream",
            "x-vqd-accept": "1",
            "referer": "https://duckduckgo.com/",
            "origin": "https://duckduckgo.com",
            "connection": "keep-alive"
        },
        timeout=15.0
    )
    
    print("Sending status request...")
    response = await client.get(
        "https://duckduckgo.com/duckchat/v1/status",
        headers={"x-vqd-accept": "1"}
    )
    print("Status code:", response.status_code)
    print("Headers:")
    for k, v in response.headers.items():
        print(f"  {k}: {v}")

if __name__ == "__main__":
    asyncio.run(test())
