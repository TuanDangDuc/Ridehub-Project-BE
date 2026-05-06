import urllib.request
import urllib.parse
import json
import ssl

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

login_url = "https://api.anhchuno.id.vn/api/user/login"
data = {"username": "tuan", "password": "123"}
req = urllib.request.Request(login_url, data=json.dumps(data).encode('utf-8'), headers={'Content-Type': 'application/json'})

try:
    with urllib.request.urlopen(req, context=ctx) as response:
        res_text = response.read().decode('utf-8')
        print("Login Response:", res_text)
        res = json.loads(res_text)
        token = res.get('token')
        
        info_url = "https://api.anhchuno.id.vn/api/user/info/tuan"
        req2 = urllib.request.Request(info_url, headers={'Authorization': 'Bearer ' + token})
        with urllib.request.urlopen(req2, context=ctx) as r2:
            info = json.loads(r2.read().decode('utf-8'))
            print("Balance:", info.get('balance'))
except Exception as e:
    print(e)
