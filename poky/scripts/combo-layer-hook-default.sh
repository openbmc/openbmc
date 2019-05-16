#!/bin/sh
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Hook to add source component/revision info to commit message
# Parameter:
#   $1 patch-file
#   $2 revision
#   $3 reponame

patchfile=$1
rev=$2
reponame=$3

sed -i -e "0,/^Subject:/s#^Subject: \[PATCH\] \($reponame: \)*\(.*\)#Subject: \[PATCH\] $reponame: \2#" $patchfile
if grep -q '^Signed-off-by:' $patchfile; then
    # Insert before Signed-off-by.
    sed -i -e "0,/^Signed-off-by:/s#\(^Signed-off-by:.*\)#\(From $reponame rev: $rev\)\n\n\1#" $patchfile
else
    # Insert before final --- separator, with extra blank lines removed.
    perl -e "\$_ = join('', <>); s/^(.*\S[ \t]*)(\n|\n\s*\n)---\n/\$1\n\nFrom $reponame rev: $rev\n---\n/s; print;" $patchfile >$patchfile.tmp
    mv $patchfile.tmp $patchfile
fi
