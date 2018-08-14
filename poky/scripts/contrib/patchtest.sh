#!/bin/bash
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# patchtest: Run patchtest on commits starting at master
#
# Copyright (c) 2017, Intel Corporation.
# All rights reserved.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
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
