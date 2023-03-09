#!/bin/sh
# Purpose: Upgrade pre-release BMCs with items needed for hostconsole group
# This can be removed when there is no longer a direct upgrade path for BMCs
# which were installed with pre-release images.

# Create groups if not already present
if grep -wq hostconsole /etc/group; then
    echo "hostconsole group already exists"
else
    echo "hostconsole group does not exist, add it"
    groupadd -f hostconsole
fi

# Add the root user to the groups
if id -nG root | grep -wq hostconsole; then
    echo "root already in hostconsole"
else
    echo "root not in group hostconsole, add it"
    usermod -a -G hostconsole root
fi

# Add all users in the priv-admin group to the
# hostconsole group so that it will not break
# exiting setup for any user.
for usr in $(grep '^'priv-admin':.*$' /etc/group | cut -d: -f4 | tr ',' ' ')
do
    # Add the usr to the hostconsole group
    if id -nG "$usr" | grep -wq hostconsole; then
        echo "$usr already in hostconsole"
    else
        echo "$usr not in group hostconsole, add it"
        usermod -a -G hostconsole "$usr"
    fi
done
