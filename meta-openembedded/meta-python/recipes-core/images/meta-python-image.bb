require  meta-python-image-base.bb

SUMMARY = "meta-python build test image"

IMAGE_INSTALL += "packagegroup-meta-python \
                  packagegroup-meta-python3"
