# Load balancer server provisioning for DigitalOcean

## Install and configure HAProxy

```
apt-get install haproxy
```

### Configuration file

```
global
        log /dev/log    local0
        log /dev/log    local1 notice
        #chroot /var/lib/haproxy
        # stats socket /run/haproxy/admin.sock mode 660 level admin
        # stats timeout 30s
        user haproxy
        group haproxy
        #daemon

        # Default SSL material locations
        #ca-base /etc/ssl/certs
        #crt-base /etc/ssl/private

        # Default ciphers to use on SSL-enabled listening sockets.
        # For more information, see ciphers(1SSL).
        # ssl-default-bind-ciphers kEECDH+aRSA+AES:kRSA+AES:+AES256:RC4-SHA:!kEDH:!LOW:!EXP:!MD5:!aNULL:!eNULL

defaults
        log     global
        mode    http
        option  httpclose
        option  httplog
        option  dontlognull
        retries 3
        timeout connect 5000
        timeout client  50000
        timeout server  50000
        errorfile 400 /etc/haproxy/errors/400.http
        errorfile 403 /etc/haproxy/errors/403.http
        errorfile 408 /etc/haproxy/errors/408.http
        errorfile 500 /etc/haproxy/errors/500.http
        errorfile 502 /etc/haproxy/errors/502.http
        errorfile 503 /etc/haproxy/errors/503.http
        errorfile 504 /etc/haproxy/errors/504.http

frontend graffitabdev
    bind *:80
    mode http
    default_backend devnodes

backend devnodes
    mode http
    balance roundrobin
    option forwardfor
    option  httpclose
    #http-request set-header X-Forwarded-Port %[dst_port]
    #http-request add-header X-Forwarded-Proto https if { ssl_fc }
    #option httpchk get /status
    #http-check expect status 200
    option httpchk GET /status
    http-check expect rstring .*
    server dev01 $DEV01_PUBLIC_IP:80 check inter 30000
    server dev02 $DEV02_PUBLIC_IP:80 check inter 30000

listen stats *:1936
    stats enable
    stats uri /
    stats auth $HA_PROXY_USER:$HA_PROXY_PASSWORD
```

### Start the service
```
service haproxy start
```

## Configure HTTPS

TODO.

## Add swap

See section in `app-server.md`.

