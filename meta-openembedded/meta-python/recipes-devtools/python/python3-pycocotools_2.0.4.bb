SUMMARY = "COCO is a large image dataset designed for object detection, segmentation, \
           person keypoints detection, stuff segmentation, and caption generation."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://pycocotools/coco.py;beginline=45;endline=45;md5=12cb539683cf245df8b6ce782d78f647"

inherit setuptools3 pypi

SRC_URI[sha256sum] = "2ab586aa389b9657b6d73c2b9a827a3681f8d00f36490c2e8ab05902e3fd9e93"

DEPENDS = "python3-cython-native python3-numpy-native virtual/crypt"
RDEPENDS:${PN} = "python3-matplotlib python3-pillow python3-profile"
