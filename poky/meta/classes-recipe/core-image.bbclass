# Common code for generating core reference images
#
# Copyright (C) 2007-2011 Linux Foundation
#
# SPDX-License-Identifier: MIT

# IMAGE_FEATURES control content of the core reference images
# 
# By default we install packagegroup-core-boot and packagegroup-base-extended packages;
# this gives us working (console only) rootfs.
#
# Available IMAGE_FEATURES:
#
# - weston              - Weston Wayland compositor
# - x11                 - X server
# - x11-base            - X server with minimal environment
# - x11-sato            - OpenedHand Sato environment
# - tools-debug         - debugging tools
# - eclipse-debug       - Eclipse remote debugging support
# - tools-profile       - profiling tools
# - tools-testapps      - tools usable to make some device tests
# - tools-sdk           - SDK (C/C++ compiler, autotools, etc.)
# - nfs-server          - NFS server
# - nfs-client          - NFS client
# - ssh-server-dropbear - SSH server (dropbear)
# - ssh-server-openssh  - SSH server (openssh)
# - hwcodecs            - Install hardware acceleration codecs
# - package-management  - installs package management tools and preserves the package manager database
# - debug-tweaks        - makes an image suitable for development, e.g. allowing passwordless root logins
#   - empty-root-password
#   - allow-empty-password
#   - allow-root-login
#   - post-install-logging
# - serial-autologin-root - with 'empty-root-password': autologin 'root' on the serial console
# - dev-pkgs            - development packages (headers, etc.) for all installed packages in the rootfs
# - dbg-pkgs            - debug symbol packages for all installed packages in the rootfs
# - lic-pkgs            - license packages for all installed pacakges in the rootfs, requires
#                         LICENSE_CREATE_PACKAGE="1" to be set when building packages too
# - doc-pkgs            - documentation packages for all installed packages in the rootfs
# - bash-completion-pkgs - bash-completion packages for recipes using bash-completion bbclass
# - ptest-pkgs          - ptest packages for all ptest-enabled recipes
# - read-only-rootfs    - tweaks an image to support read-only rootfs
# - stateless-rootfs    - systemctl-native not run, image populated by systemd at runtime
# - splash              - bootup splash screen
#
FEATURE_PACKAGES_weston = "packagegroup-core-weston"
FEATURE_PACKAGES_x11 = "packagegroup-core-x11"
FEATURE_PACKAGES_x11-base = "packagegroup-core-x11-base"
FEATURE_PACKAGES_x11-sato = "packagegroup-core-x11-sato"
FEATURE_PACKAGES_tools-debug = "packagegroup-core-tools-debug"
FEATURE_PACKAGES_eclipse-debug = "packagegroup-core-eclipse-debug"
FEATURE_PACKAGES_tools-profile = "packagegroup-core-tools-profile"
FEATURE_PACKAGES_tools-testapps = "packagegroup-core-tools-testapps"
FEATURE_PACKAGES_tools-sdk = "packagegroup-core-sdk packagegroup-core-standalone-sdk-target"
FEATURE_PACKAGES_nfs-server = "packagegroup-core-nfs-server"
FEATURE_PACKAGES_nfs-client = "packagegroup-core-nfs-client"
FEATURE_PACKAGES_ssh-server-dropbear = "packagegroup-core-ssh-dropbear"
FEATURE_PACKAGES_ssh-server-openssh = "packagegroup-core-ssh-openssh"
FEATURE_PACKAGES_hwcodecs = "${MACHINE_HWCODECS}"


# IMAGE_FEATURES_REPLACES_foo = 'bar1 bar2'
# Including image feature foo would replace the image features bar1 and bar2
IMAGE_FEATURES_REPLACES_ssh-server-openssh = "ssh-server-dropbear"
# Do not install openssh complementary packages if either packagegroup-core-ssh-dropbear or dropbear
# is installed # to avoid openssh-dropbear conflict
# see [Yocto #14858] for more information
PACKAGE_EXCLUDE_COMPLEMENTARY:append = "${@bb.utils.contains_any('PACKAGE_INSTALL', 'packagegroup-core-ssh-dropbear dropbear', ' openssh', '' , d)}"

# IMAGE_FEATURES_CONFLICTS_foo = 'bar1 bar2'
# An error exception would be raised if both image features foo and bar1(or bar2) are included

MACHINE_HWCODECS ??= ""

CORE_IMAGE_BASE_INSTALL = '\
    packagegroup-core-boot \
    packagegroup-base-extended \
    \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    '

CORE_IMAGE_EXTRA_INSTALL ?= ""

IMAGE_INSTALL ?= "${CORE_IMAGE_BASE_INSTALL}"

inherit image
