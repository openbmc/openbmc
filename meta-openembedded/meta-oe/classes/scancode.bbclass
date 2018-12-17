# We use scancode utlity for extacting licence information.
# scancode itself is an OSS Utlitity.
# For more informaiton https://github.com/nexB/scancode-toolkit

SCANCODE_FORMAT ?= "html-app"
EXT = "${@'html' if d.getVar('SCANCODE_FORMAT', True) == 'html-app' else 'json'}"
SCANCODE_TOOLKIT = "${@get_scancode_toolkit(d)}"
SCANCODE_TAG = "v2.2.1"
SCANCODE_GIT_LOCATION ?= "https://github.com/nexB/scancode-toolkit.git"
SCANCODE_SRC_LOCATION ?= "${DL_DIR}/scancode"

def get_scancode_toolkit(d):
    lf = bb.utils.lockfile(d.getVar('SCANCODE_SRC_LOCATION', True) + ".lock")
    if (not os.path.exists(d.getVar('SCANCODE_SRC_LOCATION', True))):
        os.system("git clone %s %s -b %s" % (d.getVar('SCANCODE_GIT_LOCATION', True), d.getVar('SCANCODE_SRC_LOCATION', True), d.getVar('SCANCODE_TAG', True)))
    bb.utils.unlockfile(lf)
    return (d.getVar('SCANCODE_SRC_LOCATION', True))

do_scancode() {
	mkdir -p ${DEPLOY_DIR_IMAGE}/scancode
	cd ${SCANCODE_TOOLKIT}
	if [ -d "${S}" ]; then
		./scancode ${S} --format  ${SCANCODE_FORMAT} ${DEPLOY_DIR_IMAGE}/scancode/${PN}.${EXT}
	fi
}

addtask scancode after do_patch

do_scancode_oss() {
    echo "We are done running scancode"
}

do_scancode_oss[recrdeptask] = "do_scancode_oss do_scancode"
do_scancode_oss[nostamp] = "1"
addtask do_scancode_oss after do_scancode
