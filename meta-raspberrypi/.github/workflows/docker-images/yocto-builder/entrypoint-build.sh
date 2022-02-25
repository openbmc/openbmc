#!/bin/sh

# SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>
#
# SPDX-License-Identifier: MIT

set -ex

# shellcheck disable=SC1091
. /utils.sh

META_RASPBERRYPI_PATH="/work"

[ -n "$BASE_REF" ] ||
    error "Target branch is needed. Make sure that is set in BASE_REF."
[ -d "$META_RASPBERRYPI_PATH/.git" ] ||
    error "Can't find a git checkout under $META_RASPBERRYPI_PATH ."
[ -n "$MACHINE" ] ||
    error "Machine to be used for build not provided."
[ -n "$IMAGE" ] ||
    error "Image to build not provided."

TEMP_DIR="$(mktemp -d)"
cd "$TEMP_DIR"

REPOS=" \
    git://git.yoctoproject.org/poky.git \
"
for repo in $REPOS; do
    log "Cloning $repo on branch $BASE_REF..."
    git clone --depth 1 --branch "$BASE_REF" "$repo"
done

# shellcheck disable=SC1091,SC2240
. ./poky/oe-init-build-env build

# Build configuration
printf "\n# ------ ci ------\n" >> conf/local.conf
[ -z "$SSTATE_DIR" ] || echo SSTATE_DIR = \""$SSTATE_DIR"\" >> conf/local.conf
[ -z "$DL_DIR" ] || echo DL_DIR = \""$DL_DIR"\" >> conf/local.conf
[ -z "$DISTRO" ] || echo DISTRO = \""$DISTRO"\" >> conf/local.conf
cat <<EOCONF >>conf/local.conf
BB_NUMBER_THREADS = "6"
PARALLEL_MAKE = "-j 6"
DISTRO_FEATURES:append = " systemd"
VIRTUAL-RUNTIME_init_manager = "systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED:append = " sysvinit"
VIRTUAL-RUNTIME_initscripts = "systemd-compat-units"
EOCONF

# Add the BSP layer
bitbake-layers add-layer "$META_RASPBERRYPI_PATH"

# Log configs for debugging purposes
for f in 'conf/local.conf' 'conf/bblayers.conf'; do
    printf "\n------ %s ------\n" "$f"
    cat "$f"
done

# Fire!
MACHINE="$MACHINE" bitbake "$IMAGE"
