# Copyright (C) 2020 Madhavan Krishnan <madhavan.krishnan@linaro.org>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "libcamera image"
LICENSE = "MIT"

require  meta-multimedia-image.bb

IMAGE_INSTALL += " \
        kernel-modules \
        xkeyboard-config \
"

IMAGE_INSTALL_append = "\
   libcamera \
   gstreamer1.0-plugins-good \
   gstreamer1.0-plugins-base \
"

