#!/bin/bash
#
# patchtest: Run patchtest on commits starting at master
#
# Copyright (c) 2017, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-or-later
#

set -o errexit

# Default values
pokydir=''

usage() {
CMD=$(basename $0)
cat <<EOM
Usage: $CMD [-h] [-p pokydir]
  -p pokydir  Defaults to current directory
EOM
>&2
    exit 1
}

function clone() {
    local REPOREMOTE=$1
    local REPODIR=$2
    if [ ! -d $REPODIR ]; then
	git clone $REPOREMOTE $REPODIR --quiet
    else
	( cd $REPODIR; git pull --quiet )
    fi
}

while getopts ":p:h" opt; do
    case $opt in
	p)
	    pokydir=$OPTARG
	    ;;
	h)
	    usage
	    ;;
	\?)
	    echo "Invalid option: -$OPTARG" >&2
	    usage
	    ;;
	:)
	    echo "Option -$OPTARG requires an argument." >&2
	    usage
	    ;;
    esac
done
shift $((OPTIND-1))

CDIR="$PWD"

# default pokydir to current directory if user did not specify one
if [ -z "$pokydir" ]; then
    pokydir="$CDIR"
fi

PTENV="$PWD/patchtest"
PT="$PTENV/patchtest"
PTOE="$PTENV/patchtest-oe"

if ! which virtualenv > /dev/null; then
    echo "Install virtualenv before proceeding"
    exit 1;
fi

# activate the virtual env
virtualenv $PTENV --quiet
source $PTENV/bin/activate

cd $PTENV

# clone or pull
clone git://git.yoctoproject.org/patchtest $PT
clone git://git.yoctoproject.org/patchtest-oe $PTOE

# install requirements
pip install -r $PT/requirements.txt --quiet
pip install -r $PTOE/requirements.txt --quiet

PATH="$PT:$PT/scripts:$PATH"

# loop through parent to HEAD and execute patchtest on each commit
for commit in $(git rev-list master..HEAD --reverse)
do
    shortlog="$(git log "$commit^1..$commit" --pretty='%h: %aN: %cd: %s')"
    log="$(git format-patch "$commit^1..$commit" --stdout | patchtest - -r $pokydir -s $PTOE/tests --base-commit $commit^1 --json 2>/dev/null | create-summary --fail --only-results)"
    if [ -z "$log" ]; then
	shortlog="$shortlog: OK"
    else
	shortlog="$shortlog: FAIL"
    fi
    echo "$shortlog"
    echo "$log" | sed -n -e '/Issue/p' -e '/Suggested fix/p'
    echo ""
done

deactivate

cd $CDIR
