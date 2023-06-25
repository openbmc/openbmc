#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

from abc import ABCMeta, abstractmethod
from oe.utils import execute_pre_post_process
from oe.manifest import *
from oe.package_manager import *
import os
import traceback

class Sdk(object, metaclass=ABCMeta):
    def __init__(self, d, manifest_dir):
        self.d = d
        self.sdk_output = self.d.getVar('SDK_OUTPUT')
        self.sdk_native_path = self.d.getVar('SDKPATHNATIVE').strip('/')
        self.target_path = self.d.getVar('SDKTARGETSYSROOT').strip('/')
        self.sysconfdir = self.d.getVar('sysconfdir').strip('/')

        self.sdk_target_sysroot = os.path.join(self.sdk_output, self.target_path)
        self.sdk_host_sysroot = self.sdk_output

        if manifest_dir is None:
            self.manifest_dir = self.d.getVar("SDK_DIR")
        else:
            self.manifest_dir = manifest_dir

        self.remove(self.sdk_output, True)

        self.install_order = Manifest.INSTALL_ORDER

    @abstractmethod
    def _populate(self):
        pass

    def populate(self):
        self.mkdirhier(self.sdk_output)

        # call backend dependent implementation
        self._populate()

        # Don't ship any libGL in the SDK
        self.remove(os.path.join(self.sdk_output, self.sdk_native_path,
                         self.d.getVar('libdir_nativesdk').strip('/'),
                         "libGL*"))

        # Fix or remove broken .la files
        self.remove(os.path.join(self.sdk_output, self.sdk_native_path,
                         self.d.getVar('libdir_nativesdk').strip('/'),
                         "*.la"))

        # Link the ld.so.cache file into the hosts filesystem
        link_name = os.path.join(self.sdk_output, self.sdk_native_path,
                                 self.sysconfdir, "ld.so.cache")
        self.mkdirhier(os.path.dirname(link_name))
        os.symlink("/etc/ld.so.cache", link_name)

        execute_pre_post_process(self.d, self.d.getVar('SDK_POSTPROCESS_COMMAND'))

    def movefile(self, sourcefile, destdir):
        try:
            # FIXME: this check of movefile's return code to None should be
            # fixed within the function to use only exceptions to signal when
            # something goes wrong
            if (bb.utils.movefile(sourcefile, destdir) == None):
                raise OSError("moving %s to %s failed"
                        %(sourcefile, destdir))
        #FIXME: using umbrella exc catching because bb.utils method raises it
        except Exception as e:
            bb.debug(1, "printing the stack trace\n %s" %traceback.format_exc())
            bb.fatal("unable to place %s in final SDK location" % sourcefile)

    def mkdirhier(self, dirpath):
        try:
            bb.utils.mkdirhier(dirpath)
        except OSError as e:
            bb.debug(1, "printing the stack trace\n %s" %traceback.format_exc())
            bb.fatal("cannot make dir for SDK: %s" % dirpath)

    def remove(self, path, recurse=False):
        try:
            bb.utils.remove(path, recurse)
        #FIXME: using umbrella exc catching because bb.utils method raises it
        except Exception as e:
            bb.debug(1, "printing the stack trace\n %s" %traceback.format_exc())
            bb.warn("cannot remove SDK dir: %s" % path)

    def install_locales(self, pm):
        linguas = self.d.getVar("SDKIMAGE_LINGUAS")
        if linguas:
            import fnmatch
            # Install the binary locales
            if linguas == "all":
                pm.install_glob("nativesdk-glibc-binary-localedata-*.utf-8", sdk=True)
            else:
                pm.install(["nativesdk-glibc-binary-localedata-%s.utf-8" % \
                           lang for lang in linguas.split()])
            # Generate a locale archive of them
            target_arch = self.d.getVar('SDK_ARCH')
            rootfs = oe.path.join(self.sdk_host_sysroot, self.sdk_native_path)
            localedir = oe.path.join(rootfs, self.d.getVar("libdir_nativesdk"), "locale")
            generate_locale_archive(self.d, rootfs, target_arch, localedir)
            # And now delete the binary locales
            pkgs = fnmatch.filter(pm.list_installed(), "nativesdk-glibc-binary-localedata-*.utf-8")
            pm.remove(pkgs)
        else:
            # No linguas so do nothing
            pass


def sdk_list_installed_packages(d, target, rootfs_dir=None):
    if rootfs_dir is None:
        sdk_output = d.getVar('SDK_OUTPUT')
        target_path = d.getVar('SDKTARGETSYSROOT').strip('/')

        rootfs_dir = [sdk_output, os.path.join(sdk_output, target_path)][target is True]

    if target is False:
        ipkgconf_sdk_target = d.getVar("IPKGCONF_SDK")
        d.setVar("IPKGCONF_TARGET", ipkgconf_sdk_target)

    img_type = d.getVar('IMAGE_PKGTYPE')
    import importlib
    cls = importlib.import_module('oe.package_manager.' + img_type)
    return cls.PMPkgsList(d, rootfs_dir).list_pkgs()

def populate_sdk(d, manifest_dir=None):
    env_bkp = os.environ.copy()

    img_type = d.getVar('IMAGE_PKGTYPE')
    import importlib
    cls = importlib.import_module('oe.package_manager.' + img_type + '.sdk')
    cls.PkgSdk(d, manifest_dir).populate()

    os.environ.clear()
    os.environ.update(env_bkp)

def get_extra_sdkinfo(sstate_dir):
    """
    This function is going to be used for generating the target and host manifest files packages of eSDK.
    """
    import math
    
    extra_info = {}
    extra_info['tasksizes'] = {}
    extra_info['filesizes'] = {}
    for root, _, files in os.walk(sstate_dir):
        for fn in files:
            if fn.endswith('.tgz'):
                fsize = int(math.ceil(float(os.path.getsize(os.path.join(root, fn))) / 1024))
                task = fn.rsplit(':',1)[1].split('_',1)[1].split(',')[0]
                origtotal = extra_info['tasksizes'].get(task, 0)
                extra_info['tasksizes'][task] = origtotal + fsize
                extra_info['filesizes'][fn] = fsize
    return extra_info

if __name__ == "__main__":
    pass
