#
# Copyright (C) 2007 OpenedHand Ltd.
# Copyright (C) 2012 Red Hat, Inc.
#

SUMMARY = "Essential build dependencies"

inherit packagegroup

RDEPENDS_packagegroup-core-buildessential = "\
    autoconf \
    automake \
    binutils \
    binutils-symlinks \
    cpp \
    cpp-symlinks \
    gcc \
    gcc-symlinks \
    g++ \
    g++-symlinks \
    gettext \
    make \
    libstdc++ \
    libstdc++-dev \
    libtool \
    pkgconfig \
    "

