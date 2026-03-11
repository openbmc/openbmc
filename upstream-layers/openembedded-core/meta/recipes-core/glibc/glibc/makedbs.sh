#!/bin/sh

#
# Make passwd.db, group.db, etc.
#

VAR_DB=/var/db

# Use make if available
if [ -x /usr/bin/make -o -x /bin/make ]; then
	make -C $VAR_DB
	exit 0
fi

# No make available, do it in hard way

# passwd.db
if [ -e /etc/passwd ]; then
target=$VAR_DB/passwd.db
echo -n "passwd... "
awk 'BEGIN { FS=":"; OFS=":" } \
 /^[ \t]*$$/ { next } \
 /^[ \t]*#/ { next } \
 /^[^#]/ { printf ".%s ", $$1; print; \
	   printf "=%s ", $$3; print }' /etc/passwd | \
makedb --quiet -o $target -
echo "done."
fi

# group.db
if [ -e /etc/group ]; then
target=$VAR_DB/group.db
echo -n "group... "
awk 'BEGIN { FS=":"; OFS=":" } \
 /^[ \t]*$$/ { next } \
 /^[ \t]*#/ { next } \
 /^[^#]/ { printf ".%s ", $$1; print; \
	   printf "=%s ", $$3; print; \
	   if ($$4 != "") { \
	     split($$4, grmems, ","); \
	     for (memidx in grmems) { \
	       mem=grmems[memidx]; \
	       if (members[mem] == "") \
		 members[mem]=$$3; \
	       else \
		 members[mem]=members[mem] "," $$3; \
	     } \
	     delete grmems; } } \
 END { for (mem in members) \
	 printf ":%s %s %s\n", mem, mem, members[mem]; }' /etc/group | \
makedb --quiet -o $target -
echo "done."
fi

# ethers.db
if [ -e /etc/ethers ]; then
target=$VAR_DB/ethers.db
echo -n "ethers... "
awk '/^[ \t]*$$/ { next } \
 /^[ \t]*#/ { next } \
 /^[^#]/ { printf ".%s ", $$1; print; \
	   printf "=%s ", $$2; print }' /etc/ethers | \
makedb --quiet -o $target -
echo "done."
fi

# protocols.db
if [ -e /etc/protocols ]; then
target=$VAR_DB/protocols.db
echo -n "protocols... "
awk '/^[ \t]*$$/ { next } \
 /^[ \t]*#/ { next } \
 /^[^#]/ { printf ".%s ", $$1; print; \
	   printf "=%s ", $$2; print; \
	   for (i = 3; i <= NF && !($$i ~ /^#/); ++i) \
	     { printf ".%s ", $$i; print } }' /etc/protocols | \
makedb --quiet -o $target -
echo "done."
fi

# rpc.db
if [ -e /etc/rpc ]; then
target=$VAR_DB/rpc.db
echo -n "rpc... "
awk '/^[ \t]*$$/ { next } \
 /^[ \t]*#/ { next } \
 /^[^#]/ { printf ".%s ", $$1; print; \
	   printf "=%s ", $$2; print; \
	   for (i = 3; i <= NF && !($$i ~ /^#/); ++i) \
	     { printf ".%s ", $$i; print } }' /etc/rpc | \
makedb --quiet -o $target -
echo "done."
fi

# services.db
if [ -e /etc/services ]; then
target=$VAR_DB/services.db
echo -n "services... "
awk 'BEGIN { FS="[ \t/]+" } \
 /^[ \t]*$$/ { next } \
 /^[ \t]*#/ { next } \
 /^[^#]/ { sub(/[ \t]*#.*$$/, "");\
	   printf ":%s/%s ", $$1, $$3; print; \
	   printf ":%s/ ", $$1; print; \
	   printf "=%s/%s ", $$2, $$3; print; \
	   printf "=%s/ ", $$2; print; \
	   for (i = 4; i <= NF && !($$i ~ /^#/); ++i) \
	     { printf ":%s/%s ", $$i, $$3; print; \
	       printf ":%s/ ", $$i; print } }' /etc/services | \
makedb --quiet -o $target -
echo "done."
fi

# shadow.db
if [ -e /etc/shadow ]; then
target=$VAR_DB/shadow.db
echo -n "shadow... "
awk 'BEGIN { FS=":"; OFS=":" } \
 /^[ \t]*$$/ { next } \
 /^[ \t]*#/ { next } \
 /^[^#]/ { printf ".%s ", $$1; print }' /etc/shadow | \
(umask 077 && makedb --quiet -o $target -)
echo "done."
if chgrp shadow $target 2>/dev/null; then
	chmod g+r $target
else
	chown 0 $target; chgrp 0 $target; chmod 600 $target;
	echo
	echo "Warning: The shadow password database $target"
	echo "has been set to be readable only by root.  You may want"
	echo "to make it readable by the \`shadow' group depending"
	echo "on your configuration."
	echo
fi
fi

# gshadow.db
if [ -e /etc/gshadow ]; then
target=$VAR_DB/gshadow.db
echo -n "gshadow... "
awk 'BEGIN { FS=":"; OFS=":" } \
 /^[ \t]*$$/ { next } \
 /^[ \t]*#/ { next } \
 /^[^#]/ { printf ".%s ", $$1; print }' /etc/gshadow | \
(umask 077 && makedb --quiet -o $target -)
echo "done."
if chgrp shadow $target 2>/dev/null; then
	chmod g+r $target
else
	chown 0 $target; chgrp 0 $target; chmod 600 $target
	echo
	echo "Warning: The shadow group database $target"
	echo "has been set to be readable only by root.  You may want"
	echo "to make it readable by the \`shadow' group depending"
	echo "on your configuration."
	echo
fi
fi

# netgroup.db
if [ -e /etc/netgroup ]; then
target=$VAR_DB/netgroup.db
echo -n "netgroup... "
awk 'BEGIN { ini=1 } \
 /^[ \t]*$$/ { next } \
 /^[ \t]*#/ { next } \
 /^[^#]/ { if (sub(/[ \t]*\\$$/, " ") == 0) end="\n"; \
	   else end=""; \
	   gsub(/[ \t]+/, " "); \
	   sub(/^[ \t]*/, ""); \
	   if (ini == 0) printf "%s%s", $$0, end; \
	   else printf ".%s %s%s", $$1, $$0, end; \
	   ini=end == "" ? 0 : 1; } \
 END { if (ini==0) printf "\n" }' /etc/netgroup | \
makedb --quiet -o $target
echo "done."
fi
