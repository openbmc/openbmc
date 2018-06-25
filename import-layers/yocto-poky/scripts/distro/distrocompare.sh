#!/usr/bin/env bash
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
# distrocompare.sh : provides capability to get a list of new packages
#                    based on two distinct branches. This script takes
#                    2 parameters; either a commit-ish or a branch name
#
#                    To run : distrocompare.sh <older hash> <newer hash>
#                    E.g. distrocompare.sh morty 92aa0e7
#                    E.g. distrocompare.sh morty pyro
#

# get input as version
previous_version=$1
current_version=$2

# set previous and current version
if [ -z "$2" ]; then
    previous_version=$1
    current_version="current"
fi

# get script location. That's where the source supposedly located as well.
scriptdir="$( realpath $(dirname "${BASH_SOURCE[0]}" ))"
sourcedir="$( realpath $scriptdir/../.. )"

# create working directory
workdir=$(mktemp -d)

# prepare to rollback to the branch if not similar
branch=`cd $sourcedir; git branch | grep \* | cut -d ' ' -f2`

# set current workdir to store final result
currentworkdir=`pwd`

# persists the file after local repo change
cp $scriptdir/build-recipe-list.py $workdir

#==================================================================

function bake_distrodata {
    # get to source directory of the git
    cd $sourcedir

    # change the branch / commit. Do not change if input is current
    if [ "$1" != "current" ]; then
        output=$(git checkout $1 2>&1)

        # exit if git fails
        if [[ $output == *"error"* ]]; then
            echo "git error : $output"
            echo "exiting ... "
            rm -rf $workdir
            exit
        fi
    fi

    # make tmp as workdir
    cd $workdir

    # source oe-init to generate a new build folder
    source $sourcedir/oe-init-build-env $1

    # if file already exists with distrodata, do not append
    if ! grep -q "distrodata" "conf/local.conf"; then
        # add inherit distrodata to local.conf to enable distrodata feature
        echo 'INHERIT += "distrodata"' >> conf/local.conf
    fi

    # use from tmp
    $workdir/build-recipe-list.py generate_recipe_list
}

bake_distrodata $previous_version
bake_distrodata $current_version

#==================================================================

cd $workdir

# compare the 2 generated recipe-list.txt
$workdir/build-recipe-list.py compare_recipe $previous_version $current_version

# copy final result to current working directory
cp $workdir/*_new_recipe_list.txt $currentworkdir

if [ $? -ne 0 ]; then
    rm -rf $workdir/$previous_version
    rm -rf $workdir/$current_version
    rm $workdir/build-recipe-list.py
    # preserve the result in /tmp/distrodata if fail to copy the result over
    exit
fi

# cleanup
rm -rf $workdir

# perform rollback branch
cd $sourcedir
currentbranch=`git branch | grep \* | cut -d ' ' -f2`
if [ "$currentbranch" != "$branch" ]; then
    git checkout $branch
fi

cd $currentworkdir

#==================================================================
