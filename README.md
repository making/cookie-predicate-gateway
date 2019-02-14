```
# Route to New UI
curl -v -H "Host: demo.example.com" -H "Cookie: B=4ee55ebc-9b42-4bce-b484-34254515b9a5" localhost:9999/get
```


```
# Route to Old UI
curl -v -H "Host: demo.example.com" -H "Cookie: B=4ee55ebc-9b42-4bce-b484-34254515b9a0" localhost:9999/get
```


```
# Random
curl -v -H "Host: demo.example.com" -H "Cookie: B=$(uuidgen)" localhost:9999/get
```