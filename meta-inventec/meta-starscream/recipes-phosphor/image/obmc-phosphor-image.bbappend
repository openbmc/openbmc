inherit extrausers

EXTRA_USERS_PARAMS:append = " \
useradd -e '' -ou 0 -d /home/root -G priv-admin,root,sudo,ipmi,web,redfish -p 'gzW59equAcJAg' sysadmin; \
useradd -e '' -ou 0 -d /home/root -G priv-admin,root,sudo,ipmi,web,redfish -p 'kFdHdjRkot8KQ' admin; \
"
