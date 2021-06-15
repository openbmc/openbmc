SUMMARY = "COCO is a large image dataset designed for object detection, segmentation, \
           person keypoints detection, stuff segmentation, and caption generation."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://pycocotools/coco.py;beginline=45;endline=45;md5=12cb539683cf245df8b6ce782d78f647"

inherit setuptools3 pypi

SRC_URI[md5sum] = "33858f23c8b99ce8116b70c32f6b6dd9"
SRC_URI[sha256sum] = "24717a12799b4471c2e54aa210d642e6cd4028826a1d49fcc2b0e3497e041f1a"

DEPENDS = "python3-cython-native python3-numpy-native virtual/crypt"
RDEPENDS_${PN} = "python3-matplotlib python3-pillow python3-profile"
