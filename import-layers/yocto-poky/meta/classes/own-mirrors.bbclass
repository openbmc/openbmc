PREMIRRORS_prepend = " \
cvs://.*/.*     ${SOURCE_MIRROR_URL} \n \
svn://.*/.*     ${SOURCE_MIRROR_URL} \n \
git://.*/.*     ${SOURCE_MIRROR_URL} \n \
gitsm://.*/.*   ${SOURCE_MIRROR_URL} \n \
hg://.*/.*      ${SOURCE_MIRROR_URL} \n \
bzr://.*/.*     ${SOURCE_MIRROR_URL} \n \
p4://.*/.*      ${SOURCE_MIRROR_URL} \n \
osc://.*/.*     ${SOURCE_MIRROR_URL} \n \
https?$://.*/.* ${SOURCE_MIRROR_URL} \n \
ftp://.*/.*     ${SOURCE_MIRROR_URL} \n \
npm://.*/?.*    ${SOURCE_MIRROR_URL} \n \
"
