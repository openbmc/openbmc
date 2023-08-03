#!/bin/sh
# Purpose: creates /home/admin directory and updates admin user
#          home directory to /home/admin

# Update admin user home directory
if id -u "admin" >/dev/null 2>&1; then
    # admin user exists
    homedir=$(grep '^admin:' /etc/passwd | cut -d: -f6)
    if test "${homedir}" = "/"; then
        echo "Changing admin user home directory"
        mkdir -p /home/admin
        chmod 0755 /home/admin
        chown --recursive admin:admin /home/admin
        usermod --home /home/admin admin
    else
        echo "admin user home directory is okay"
    fi
else
    echo "admin user account is not present"
fi
