#!/usr/bin/env bash

source <(bitbake -e gem5-aarch64-native | grep \
    -e "^STAGING_DATADIR_NATIVE=" -e "^DEPLOY_DIR_TOOLS=")

# Used by baremetal_atp.py
export GEM5_DATADIR=${STAGING_DATADIR_NATIVE}/gem5
export ATP_DATADIR=${STAGING_DATADIR_NATIVE}/gem5

# Fast-forward Linux boot and restore into timing simulation
${DEPLOY_DIR_TOOLS}/start-gem5.sh --checkpoint $@
${DEPLOY_DIR_TOOLS}/start-gem5.sh --restore-with-cpu TimingSimpleCPU \
                                  --checkpoint-restore 1 $@
