# Copyright (C) 2013 - 2016 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# testsdk.bbclass enables testing for SDK and Extensible SDK
#
# To run SDK tests, run the commands:
# $ bitbake <image-name> -c populate_sdk
# $ bitbake <image-name> -c testsdk
#
# To run eSDK tests, run the commands:
# $ bitbake <image-name> -c populate_sdk_ext
# $ bitbake <image-name> -c testsdkext
#
# where "<image-name>" is an image like core-image-sato.

TESTSDK_CLASS_NAME ?= "oeqa.sdk.testsdk.TestSDK"
TESTSDKEXT_CLASS_NAME ?= "oeqa.sdkext.testsdk.TestSDKExt"

def import_and_run(name, d):
    import importlib

    class_name = d.getVar(name)
    if class_name:
        module, cls = class_name.rsplit('.', 1)
        m = importlib.import_module(module)
        c = getattr(m, cls)()
        c.run(d)
    else:
        bb.warn('No tests were run because %s did not define a class' % name)

import_and_run[vardepsexclude] = "DATETIME BB_ORIGENV"

python do_testsdk() {
    import_and_run('TESTSDK_CLASS_NAME', d)
}
addtask testsdk
do_testsdk[nostamp] = "1"
do_testsdk[network] = "1"

python do_testsdkext() {
    import_and_run('TESTSDKEXT_CLASS_NAME', d)
}
addtask testsdkext
do_testsdkext[nostamp] = "1"
do_testsdkext[network] = "1"

python () {
    if oe.types.boolean(d.getVar("TESTIMAGE_AUTO") or "False"):
        bb.build.addtask("testsdk", None, "do_populate_sdk", d)
        bb.build.addtask("testsdkext", None, "do_populate_sdk_ext", d)
}
