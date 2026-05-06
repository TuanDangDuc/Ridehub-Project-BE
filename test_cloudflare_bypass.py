import urllib.request
import urllib.parse
import json
import ssl

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

url = "https://pay.sepay.vn/v1/checkout/init"
headers = {
    "Authorization": "Bearer 8EGFQ6TTGHVRYXBZHJKK8Y4STWZFK3XQ4AXP0CIKJOWOD3VNBG7NCVD1JLS1BFIO",
    "Content-Type": "application/x-www-form-urlencoded",
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
}
data = {
    "merchant": "SP-LIVE-MA649336",
    "operation": "PURCHASE",
    "order_invoice_number": "TOPUP-123",
    "order_amount": "10000",
    "currency": "VND",
    "order_description": "test",
    "signature": "fake_signature"
}
data_encoded = urllib.parse.urlencode(data).encode('utf-8')
req = urllib.request.Request(url, data=data_encoded, headers=headers, method='POST')
try:
    with urllib.request.urlopen(req, context=ctx) as f:
        print("Status:", f.status)
        print(f.read().decode('utf-8'))
except urllib.error.HTTPError as e:
    print("HTTPError Status:", e.code)
    print("Headers:", e.headers)
except Exception as e:
    print(e)
