# Common code for generating core reference images
#
# Copyright (C) 2007-2011 Linux Foundation
#
# SPDX-License-Identifier: MIT

# IMAGE_FEATURES control the content of the core reference images
# 
# By default we install packagegroup-core-boot and packagegroup-base-extended packages;
# this gives us a working (console only) rootfs.
#
# Available IMAGE_FEATURES:
#
# These features install additional packages into the rootfs:
# - eclipse-debug       - Eclipse remote debugging support
# - hwcodecs            - hardware acceleration codecs (specified in MACHINE_HWCODECS)
# - nfs-client          - NFS client
# - nfs-server          - NFS server
# - package-management  - installs package management tools and preserves the package manager database
# - splash              - bootup splash screen
# - ssh-server-dropbear - SSH server (dropbear)
# - ssh-server-openssh  - SSH server (openssh)
# - tools-debug         - debugging tools
# - tools-profile       - profiling tools
# - tools-sdk           - SDK (C/C++ compiler, autotools, etc.)
# - tools-testapps      - tools usable to make some device tests
# - weston              - Weston Wayland compositor
# - x11                 - X server
# - x11-base            - X server with minimal environment
# - x11-sato            - OpenedHand Sato environment
#
# These features install complementary packages for all installed packages in the rootfs:
# - dbg-pkgs            - debug symbol packages
# - dev-pkgs            - development packages (headers, etc.)
# - doc-pkgs            - documentation packages
# - lic-pkgs            - license packages, requires LICENSE_CREATE_PACKAGE="1" to be set when building packages too
# - ptest-pkgs          - ptest packages for all ptest-enabled recipes
#
# These features install complementary development packages:
# - bash-completion-pkgs - bash-completion packages for recipes using bash-completion bbclass
# - zsh-completion-pkgs - zsh-completion packages
#
# These features tweak the behavior of the rootfs:
# - overlayfs-etc       - sets up /etc in overlayfs
# - read-only-rootfs    - tweaks an image to support a read-only rootfs
# - read-only-rootfs-delayed-postinsts - supports post-install scripts with read-only-rootfs
# - stateless-rootfs    - systemctl-native is not run, image is populated by systemd at runtime
#
# These features are for development purposes (some were previously part of the debug-tweaks feature):
# - allow-empty-password  - users can have an empty password (debug-tweaks)
# - allow-root-login      - the root user can login (debug-tweaks)
# - empty-root-password   - the root user has no password set (debug-tweaks)
# - post-install-logging  - log the output of postinstall scriptlets (debug-tweaks)
# - serial-autologin-root - with 'empty-root-password': autologin 'root' on the serial console
#
FEATURE_PACKAGES_eclipse-debug = "packagegroup-core-eclipse-debug"
FEATURE_PACKAGES_hwcodecs = "${MACHINE_HWCODECS}"
FEATURE_PACKAGES_nfs-client = "packagegroup-core-nfs-client"
FEATURE_PACKAGES_nfs-server = "packagegroup-core-nfs-server"
FEATURE_PACKAGES_ssh-server-dropbear = "packagegroup-core-ssh-dropbear"
FEATURE_PACKAGES_ssh-server-openssh = "packagegroup-core-ssh-openssh"
FEATURE_PACKAGES_tools-debug = "packagegroup-core-tools-debug"
FEATURE_PACKAGES_tools-profile = "packagegroup-core-tools-profile"
FEATURE_PACKAGES_tools-sdk = "packagegroup-core-sdk packagegroup-core-standalone-sdk-target"
FEATURE_PACKAGES_tools-testapps = "packagegroup-core-tools-testapps"
FEATURE_PACKAGES_weston = "packagegroup-core-weston"
FEATURE_PACKAGES_x11 = "packagegroup-core-x11"
FEATURE_PACKAGES_x11-base = "packagegroup-core-x11-base"
FEATURE_PACKAGES_x11-sato = "packagegroup-core-x11-sato"

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
