#!/bin/sh

# Command line debug tools like pdbg and ecmd requires
# PDBG_DTB environment variable.
# attributes tool required PDBG_DTB, PDATA_INFODB and PDATA_ATTR_OVERRIDE

export PDBG_DTB=/var/lib/phosphor-software-manager/pnor/rw/DEVTREE
export PDATA_INFODB=/usr/share/pdata/attributes_info.db
export PDATA_ATTR_OVERRIDE=/tmp/devtree_attr_override
