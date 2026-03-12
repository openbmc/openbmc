#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

PREMIRRORS:prepend = " \
svn://.*/.*     ${SOURCE_MIRROR_URL} \
git://.*/.*     ${SOURCE_MIRROR_URL} \
gitsm://.*/.*   ${SOURCE_MIRROR_URL} \
hg://.*/.*      ${SOURCE_MIRROR_URL} \
p4://.*/.*      ${SOURCE_MIRROR_URL} \
https?://.*/.*  ${SOURCE_MIRROR_URL} \
ftp://.*/.*     ${SOURCE_MIRROR_URL} \
npm://.*/?.*    ${SOURCE_MIRROR_URL} \
s3://.*/.*      ${SOURCE_MIRROR_URL} \
crate://.*/.*   ${SOURCE_MIRROR_URL} \
gs://.*/.*      ${SOURCE_MIRROR_URL} \
"
