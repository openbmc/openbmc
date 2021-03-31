SUMMARY = "An example of a multilib image"
#
# This example includes a lib32 version of bash into an otherwise standard
# sato image. It assumes a "lib32" multilib has been enabled in the user's
# configuration (see the example conf files for examples of this.)
#

# First include a base image to base things off
require recipes-sato/images/core-image-sato.bb

# Now add the multilib packages we want to install
IMAGE_INSTALL += "lib32-bash"
