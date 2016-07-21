# Provisioning for database server in DigitalOcean

## Initial setup

See same section in `app-server.md`.

## Install MySQL

* Installation, see tutorial [here](https://www.digitalocean.com/community/tutorials/a-basic-mysql-tutorial)
```
apt-get install mysql-server
```

* Configure `utf8mb4` encoding, see it [here](https://mathiasbynens.be/notes/mysql-utf8mb4)
__TODO: create a SQL script for this?__

* Import an initial database structure into this server (usually through `phpmyadmin`).

## Install phpmyadmin

See tutorial [here](https://www.digitalocean.com/community/tutorials/how-to-install-and-secure-phpmyadmin-on-ubuntu-14-04)
Username and password are stored in `environment.sh`.

## Install and configure Redis

* Installation tutorial [here](https://www.digitalocean.com/community/tutorials/how-to-configure-a-redis-cluster-on-ubuntu-14-04)
Run this to additionally install `redis-cli` for testing purposes:
```
apt-get install redis-tools
```

* Information on how to secure [here](https://www.digitalocean.com/community/tutorials/how-to-secure-your-redis-installation-on-ubuntu-14-04).

* How to use Redis brief tutorial [here](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-redis)

The current configuration file with placeholders is provided `redis.conf`. Redis should be started as:
```
$ redis-server /path/to/redis.conf &
```

To test that the installation was correct:
```
# Start up redis
$ redis-server /path/to/config &

# This should give error as we are securing it
$ redis-cli keys '*'

# This should give back an empty set (without auth errors)
$ redis-cli -a yourpassword keys '*'
```

TODO: investigate service to at boot and use of nohup

## Add firewall rules

See section in `app-server.md`

## Add swap

See section in `app-server.md`




