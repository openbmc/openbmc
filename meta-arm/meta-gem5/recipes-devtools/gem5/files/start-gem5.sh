#!/bin/bash

# Get parameters from bitbake configuration

source <(bitbake -e gem5-aarch64-native | grep \
    -e "^STAGING_.*_NATIVE=" \
    -e "^DEPLOY_DIR.*=" \
    -e "^GEM5_RUN.*=")

export M5_PATH="${DEPLOY_DIR_IMAGE}"

args=""

if [ -n "${GEM5_RUN_KERNEL}" ]; then
    kernfile=$(readlink -f ${DEPLOY_DIR_IMAGE}/${GEM5_RUN_KERNEL})
    args="$args --kernel=$kernfile"
fi

if [ -n "${GEM5_RUN_DISK}" ]; then
    diskfile=$(readlink -f ${DEPLOY_DIR_IMAGE}/${GEM5_RUN_DISK})
    args="$args --disk-image=$diskfile"
fi

if [ -n "${GEM5_RUN_DTB}" ]; then
    dtbfile=$(readlink -f ${DEPLOY_DIR_IMAGE}/${GEM5_RUN_DTB})
    args="$args --dtb=$dtbfile"
fi

if [ -n "${GEM5_RUN_CMDLINE}" ]; then
    args="$args --command-line='${GEM5_RUN_CMDLINE}'"
fi

if [ -n "${GEM5_RUN_EXTRA}" ]; then
    args="$args ${GEM5_RUN_EXTRA}"
fi

oe-run-native gem5-aarch64-native ${GEM5_RUN_CONFIG} \
    ${STAGING_DATADIR_NATIVE}/gem5/${GEM5_RUN_PROFILE} ${args} "$@"

