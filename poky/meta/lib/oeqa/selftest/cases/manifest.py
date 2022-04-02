#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import get_bb_var, bitbake

class ManifestEntry:
    '''A manifest item of a collection able to list missing packages'''
    def __init__(self, entry):
        self.file = entry
        self.missing = []

class VerifyManifest(OESelftestTestCase):
    '''Tests for the manifest files and contents of an image'''

    @classmethod
    def check_manifest_entries(self, manifest, path):
        manifest_errors = []
        try:
            with open(manifest, "r") as mfile:
                for line in mfile:
                    manifest_entry = os.path.join(path, line.split()[0])
                    self.logger.debug("{}: looking for {}"\
                                    .format(self.classname, manifest_entry))
                    if not os.path.isfile(manifest_entry):
                        manifest_errors.append(manifest_entry)
                        self.logger.debug("{}: {} not found"\
                                    .format(self.classname, manifest_entry))
        except OSError as e:
            self.logger.debug("{}: checking of {} failed"\
                    .format(self.classname, manifest))
            raise e

        return manifest_errors

    #this will possibly move from here
    @classmethod
    def get_dir_from_bb_var(self, bb_var, target = None):
        target == self.buildtarget if target == None else target
        directory = get_bb_var(bb_var, target);
        if not directory or not os.path.isdir(directory):
            self.logger.debug("{}: {} points to {} when target = {}"\
                    .format(self.classname, bb_var, directory, target))
            raise OSError
        return directory

    @classmethod
    def setUpClass(self):

        super(VerifyManifest, self).setUpClass()
        self.buildtarget = 'core-image-minimal'
        self.classname = 'VerifyManifest'

        self.logger.info("{}: doing bitbake {} as a prerequisite of the test"\
                .format(self.classname, self.buildtarget))
        if bitbake(self.buildtarget).status:
            self.logger.debug("{} Failed to setup {}"\
                    .format(self.classname, self.buildtarget))
            self.skipTest("{}: Cannot setup testing scenario"\
                    .format(self.classname))

    def test_SDK_manifest_entries(self):
        '''Verifying the SDK manifest entries exist, this may take a build'''

        # the setup should bitbake core-image-minimal and here it is required
        # to do an additional setup for the sdk
        sdktask = '-c populate_sdk'
        bbargs = sdktask + ' ' + self.buildtarget
        self.logger.debug("{}: doing bitbake {} as a prerequisite of the test"\
                .format(self.classname, bbargs))
        if bitbake(bbargs).status:
            self.logger.debug("{} Failed to bitbake {}"\
                    .format(self.classname, bbargs))
            self.skipTest("{}: Cannot setup testing scenario"\
                    .format(self.classname))


        pkgdata_dir = reverse_dir = {}
        mfilename = mpath = m_entry = {}
        # get manifest location based on target to query about
        d_target= dict(target = self.buildtarget,
                         host = 'nativesdk-packagegroup-sdk-host')
        try:
            mdir = self.get_dir_from_bb_var('SDK_DEPLOY', self.buildtarget)
            for k in d_target.keys():
                toolchain_outputname = get_bb_var('TOOLCHAIN_OUTPUTNAME', self.buildtarget)
                mfilename[k] = "{}.{}.manifest".format(toolchain_outputname, k)
                mpath[k] = os.path.join(mdir, mfilename[k])
                if not os.path.isfile(mpath[k]):
                    self.logger.debug("{}: {} does not exist".format(
                        self.classname, mpath[k]))
                    raise IOError
                m_entry[k] = ManifestEntry(mpath[k])

                pkgdata_dir[k] = self.get_dir_from_bb_var('PKGDATA_DIR',
                        d_target[k])
                reverse_dir[k] = os.path.join(pkgdata_dir[k],
                        'runtime-reverse')
                if not os.path.exists(reverse_dir[k]):
                    self.logger.debug("{}: {} does not exist".format(
                        self.classname, reverse_dir[k]))
                    raise IOError
        except OSError:
            raise self.skipTest("{}: Error in obtaining manifest dirs"\
                .format(self.classname))
        except IOError:
            msg = "{}: Error cannot find manifests in the specified dir:\n{}"\
                    .format(self.classname, mdir)
            self.fail(msg)

        for k in d_target.keys():
            self.logger.debug("{}: Check manifest {}".format(
                self.classname, m_entry[k].file))

            m_entry[k].missing = self.check_manifest_entries(\
                                               m_entry[k].file,reverse_dir[k])
            if m_entry[k].missing:
                msg = '{}: {} Error has the following missing entries'\
                        .format(self.classname, m_entry[k].file)
                logmsg = msg+':\n'+'\n'.join(m_entry[k].missing)
                self.logger.debug(logmsg)
                self.logger.info(msg)
                self.fail(logmsg)

    def test_image_manifest_entries(self):
        '''Verifying the image manifest entries exist'''

        # get manifest location based on target to query about
        try:
            mdir = self.get_dir_from_bb_var('DEPLOY_DIR_IMAGE',
                                                self.buildtarget)
            mfilename = get_bb_var("IMAGE_LINK_NAME", self.buildtarget)\
                    + ".manifest"
            mpath = os.path.join(mdir, mfilename)
            if not os.path.isfile(mpath): raise IOError
            m_entry = ManifestEntry(mpath)

            pkgdata_dir = {}
            pkgdata_dir = self.get_dir_from_bb_var('PKGDATA_DIR',
                                                self.buildtarget)
            revdir = os.path.join(pkgdata_dir, 'runtime-reverse')
            if not os.path.exists(revdir): raise IOError
        except OSError:
            raise self.skipTest("{}: Error in obtaining manifest dirs"\
                .format(self.classname))
        except IOError:
            msg = "{}: Error cannot find manifests in dir:\n{}"\
                    .format(self.classname, mdir)
            self.fail(msg)

        self.logger.debug("{}: Check manifest {}"\
                            .format(self.classname, m_entry.file))
        m_entry.missing = self.check_manifest_entries(\
                                                    m_entry.file, revdir)
        if m_entry.missing:
            msg = '{}: {} Error has the following missing entries'\
                    .format(self.classname, m_entry.file)
            logmsg = msg+':\n'+'\n'.join(m_entry.missing)
            self.logger.debug(logmsg)
            self.logger.info(msg)
            self.fail(logmsg)
