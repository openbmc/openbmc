#
# BitBake Tests for the Fetcher (fetch2/)
#
# Copyright (C) 2012 Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import hashlib
import tempfile
import collections
import os
from bb.fetch2 import URI
from bb.fetch2 import FetchMethod
import bb
from bb.tests.support.httpserver import HTTPService

def skipIfNoNetwork():
    if os.environ.get("BB_SKIP_NETTESTS") == "yes":
        return unittest.skip("Network tests being skipped")
    return lambda f: f

class URITest(unittest.TestCase):
    test_uris = {
        "http://www.google.com/index.html" : {
            'uri': 'http://www.google.com/index.html',
            'scheme': 'http',
            'hostname': 'www.google.com',
            'port': None,
            'hostport': 'www.google.com',
            'path': '/index.html',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {},
            'query': {},
            'relative': False
        },
        "http://www.google.com/index.html;param1=value1" : {
            'uri': 'http://www.google.com/index.html;param1=value1',
            'scheme': 'http',
            'hostname': 'www.google.com',
            'port': None,
            'hostport': 'www.google.com',
            'path': '/index.html',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {
                'param1': 'value1'
            },
            'query': {},
            'relative': False
        },
        "http://www.example.org/index.html?param1=value1" : {
            'uri': 'http://www.example.org/index.html?param1=value1',
            'scheme': 'http',
            'hostname': 'www.example.org',
            'port': None,
            'hostport': 'www.example.org',
            'path': '/index.html',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {},
            'query': {
                'param1': 'value1'
            },
            'relative': False
        },
        "http://www.example.org/index.html?qparam1=qvalue1;param2=value2" : {
            'uri': 'http://www.example.org/index.html?qparam1=qvalue1;param2=value2',
            'scheme': 'http',
            'hostname': 'www.example.org',
            'port': None,
            'hostport': 'www.example.org',
            'path': '/index.html',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {
                'param2': 'value2'
            },
            'query': {
                'qparam1': 'qvalue1'
            },
            'relative': False
        },
        "http://www.example.com:8080/index.html" : {
            'uri': 'http://www.example.com:8080/index.html',
            'scheme': 'http',
            'hostname': 'www.example.com',
            'port': 8080,
            'hostport': 'www.example.com:8080',
            'path': '/index.html',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {},
            'query': {},
            'relative': False
        },
        "cvs://anoncvs@cvs.handhelds.org/cvs;module=familiar/dist/ipkg" : {
            'uri': 'cvs://anoncvs@cvs.handhelds.org/cvs;module=familiar/dist/ipkg',
            'scheme': 'cvs',
            'hostname': 'cvs.handhelds.org',
            'port': None,
            'hostport': 'cvs.handhelds.org',
            'path': '/cvs',
            'userinfo': 'anoncvs',
            'username': 'anoncvs',
            'password': '',
            'params': {
                'module': 'familiar/dist/ipkg'
            },
            'query': {},
            'relative': False
        },
        "cvs://anoncvs:anonymous@cvs.handhelds.org/cvs;tag=V0-99-81;module=familiar/dist/ipkg": {
            'uri': 'cvs://anoncvs:anonymous@cvs.handhelds.org/cvs;tag=V0-99-81;module=familiar/dist/ipkg',
            'scheme': 'cvs',
            'hostname': 'cvs.handhelds.org',
            'port': None,
            'hostport': 'cvs.handhelds.org',
            'path': '/cvs',
            'userinfo': 'anoncvs:anonymous',
            'username': 'anoncvs',
            'password': 'anonymous',
            'params': collections.OrderedDict([
                ('tag', 'V0-99-81'),
                ('module', 'familiar/dist/ipkg')
            ]),
            'query': {},
            'relative': False
        },
        "file://example.diff": { # NOTE: Not RFC compliant!
            'uri': 'file:example.diff',
            'scheme': 'file',
            'hostname': '',
            'port': None,
            'hostport': '',
            'path': 'example.diff',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {},
            'query': {},
            'relative': True
        },
        "file:example.diff": { # NOTE: RFC compliant version of the former
            'uri': 'file:example.diff',
            'scheme': 'file',
            'hostname': '',
            'port': None,
            'hostport': '',
            'path': 'example.diff',
            'userinfo': '',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {},
            'query': {},
            'relative': True
        },
        "file:///tmp/example.diff": {
            'uri': 'file:///tmp/example.diff',
            'scheme': 'file',
            'hostname': '',
            'port': None,
            'hostport': '',
            'path': '/tmp/example.diff',
            'userinfo': '',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {},
            'query': {},
            'relative': False
        },
        "git:///path/example.git": {
            'uri': 'git:///path/example.git',
            'scheme': 'git',
            'hostname': '',
            'port': None,
            'hostport': '',
            'path': '/path/example.git',
            'userinfo': '',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {},
            'query': {},
            'relative': False
        },
        "git:path/example.git": {
            'uri': 'git:path/example.git',
            'scheme': 'git',
            'hostname': '',
            'port': None,
            'hostport': '',
            'path': 'path/example.git',
            'userinfo': '',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {},
            'query': {},
            'relative': True
        },
        "git://example.net/path/example.git": {
            'uri': 'git://example.net/path/example.git',
            'scheme': 'git',
            'hostname': 'example.net',
            'port': None,
            'hostport': 'example.net',
            'path': '/path/example.git',
            'userinfo': '',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {},
            'query': {},
            'relative': False
        },
        "git://tfs-example.org:22/tfs/example%20path/example.git": {
            'uri': 'git://tfs-example.org:22/tfs/example%20path/example.git',
            'scheme': 'git',
            'hostname': 'tfs-example.org',
            'port': 22,
            'hostport': 'tfs-example.org:22',
            'path': '/tfs/example path/example.git',
            'userinfo': '',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {},
            'query': {},
            'relative': False
        },
        "http://somesite.net;someparam=1": {
            'uri': 'http://somesite.net;someparam=1',
            'scheme': 'http',
            'hostname': 'somesite.net',
            'port': None,
            'hostport': 'somesite.net',
            'path': '',
            'userinfo': '',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {"someparam" : "1"},
            'query': {},
            'relative': False
        },
        "file://somelocation;someparam=1": {
            'uri': 'file:somelocation;someparam=1',
            'scheme': 'file',
            'hostname': '',
            'port': None,
            'hostport': '',
            'path': 'somelocation',
            'userinfo': '',
            'userinfo': '',
            'username': '',
            'password': '',
            'params': {"someparam" : "1"},
            'query': {},
            'relative': True
        }

    }

    def test_uri(self):
        for test_uri, ref in self.test_uris.items():
            uri = URI(test_uri)

            self.assertEqual(str(uri), ref['uri'])

            # expected attributes
            self.assertEqual(uri.scheme, ref['scheme'])

            self.assertEqual(uri.userinfo, ref['userinfo'])
            self.assertEqual(uri.username, ref['username'])
            self.assertEqual(uri.password, ref['password'])

            self.assertEqual(uri.hostname, ref['hostname'])
            self.assertEqual(uri.port, ref['port'])
            self.assertEqual(uri.hostport, ref['hostport'])

            self.assertEqual(uri.path, ref['path'])
            self.assertEqual(uri.params, ref['params'])

            self.assertEqual(uri.relative, ref['relative'])

    def test_dict(self):
        for test in self.test_uris.values():
            uri = URI()

            self.assertEqual(uri.scheme, '')
            self.assertEqual(uri.userinfo, '')
            self.assertEqual(uri.username, '')
            self.assertEqual(uri.password, '')
            self.assertEqual(uri.hostname, '')
            self.assertEqual(uri.port, None)
            self.assertEqual(uri.path, '')
            self.assertEqual(uri.params, {})


            uri.scheme = test['scheme']
            self.assertEqual(uri.scheme, test['scheme'])

            uri.userinfo = test['userinfo']
            self.assertEqual(uri.userinfo, test['userinfo'])
            self.assertEqual(uri.username, test['username'])
            self.assertEqual(uri.password, test['password'])

            # make sure changing the values doesn't do anything unexpected
            uri.username = 'changeme'
            self.assertEqual(uri.username, 'changeme')
            self.assertEqual(uri.password, test['password'])
            uri.password = 'insecure'
            self.assertEqual(uri.username, 'changeme')
            self.assertEqual(uri.password, 'insecure')

            # reset back after our trickery
            uri.userinfo = test['userinfo']
            self.assertEqual(uri.userinfo, test['userinfo'])
            self.assertEqual(uri.username, test['username'])
            self.assertEqual(uri.password, test['password'])

            uri.hostname = test['hostname']
            self.assertEqual(uri.hostname, test['hostname'])
            self.assertEqual(uri.hostport, test['hostname'])

            uri.port = test['port']
            self.assertEqual(uri.port, test['port'])
            self.assertEqual(uri.hostport, test['hostport'])

            uri.path = test['path']
            self.assertEqual(uri.path, test['path'])

            uri.params = test['params']
            self.assertEqual(uri.params, test['params'])

            uri.query = test['query']
            self.assertEqual(uri.query, test['query'])

            self.assertEqual(str(uri), test['uri'])

            uri.params = {}
            self.assertEqual(uri.params, {})
            self.assertEqual(str(uri), (str(uri).split(";"))[0])

class FetcherTest(unittest.TestCase):

    def setUp(self):
        self.origdir = os.getcwd()
        self.d = bb.data.init()
        self.tempdir = tempfile.mkdtemp()
        self.dldir = os.path.join(self.tempdir, "download")
        os.mkdir(self.dldir)
        self.d.setVar("DL_DIR", self.dldir)
        self.unpackdir = os.path.join(self.tempdir, "unpacked")
        os.mkdir(self.unpackdir)
        persistdir = os.path.join(self.tempdir, "persistdata")
        self.d.setVar("PERSISTENT_DIR", persistdir)

    def tearDown(self):
        os.chdir(self.origdir)
        if os.environ.get("BB_TMPDIR_NOCLEAN") == "yes":
            print("Not cleaning up %s. Please remove manually." % self.tempdir)
        else:
            bb.process.run('chmod u+rw -R %s' % self.tempdir)
            bb.utils.prunedir(self.tempdir)

class MirrorUriTest(FetcherTest):

    replaceuris = {
        ("git://git.invalid.infradead.org/mtd-utils.git;tag=1234567890123456789012345678901234567890", "git://.*/.*", "http://somewhere.org/somedir/") 
            : "http://somewhere.org/somedir/git2_git.invalid.infradead.org.mtd-utils.git.tar.gz",
        ("git://git.invalid.infradead.org/mtd-utils.git;tag=1234567890123456789012345678901234567890", "git://.*/([^/]+/)*([^/]*)", "git://somewhere.org/somedir/\\2;protocol=http") 
            : "git://somewhere.org/somedir/mtd-utils.git;tag=1234567890123456789012345678901234567890;protocol=http", 
        ("git://git.invalid.infradead.org/foo/mtd-utils.git;tag=1234567890123456789012345678901234567890", "git://.*/([^/]+/)*([^/]*)", "git://somewhere.org/somedir/\\2;protocol=http") 
            : "git://somewhere.org/somedir/mtd-utils.git;tag=1234567890123456789012345678901234567890;protocol=http", 
        ("git://git.invalid.infradead.org/foo/mtd-utils.git;tag=1234567890123456789012345678901234567890", "git://.*/([^/]+/)*([^/]*)", "git://somewhere.org/\\2;protocol=http") 
            : "git://somewhere.org/mtd-utils.git;tag=1234567890123456789012345678901234567890;protocol=http", 
        ("git://someserver.org/bitbake;tag=1234567890123456789012345678901234567890", "git://someserver.org/bitbake", "git://git.openembedded.org/bitbake")
            : "git://git.openembedded.org/bitbake;tag=1234567890123456789012345678901234567890",
        ("file://sstate-xyz.tgz", "file://.*", "file:///somewhere/1234/sstate-cache") 
            : "file:///somewhere/1234/sstate-cache/sstate-xyz.tgz",
        ("file://sstate-xyz.tgz", "file://.*", "file:///somewhere/1234/sstate-cache/") 
            : "file:///somewhere/1234/sstate-cache/sstate-xyz.tgz",
        ("http://somewhere.org/somedir1/somedir2/somefile_1.2.3.tar.gz", "http://.*/.*", "http://somewhere2.org/somedir3") 
            : "http://somewhere2.org/somedir3/somefile_1.2.3.tar.gz",
        ("http://somewhere.org/somedir1/somefile_1.2.3.tar.gz", "http://somewhere.org/somedir1/somefile_1.2.3.tar.gz", "http://somewhere2.org/somedir3/somefile_1.2.3.tar.gz") 
            : "http://somewhere2.org/somedir3/somefile_1.2.3.tar.gz",
        ("http://www.apache.org/dist/subversion/subversion-1.7.1.tar.bz2", "http://www.apache.org/dist", "http://archive.apache.org/dist")
            : "http://archive.apache.org/dist/subversion/subversion-1.7.1.tar.bz2",
        ("http://www.apache.org/dist/subversion/subversion-1.7.1.tar.bz2", "http://.*/.*", "file:///somepath/downloads/")
            : "file:///somepath/downloads/subversion-1.7.1.tar.bz2",
        ("git://git.invalid.infradead.org/mtd-utils.git;tag=1234567890123456789012345678901234567890", "git://.*/.*", "git://somewhere.org/somedir/BASENAME;protocol=http") 
            : "git://somewhere.org/somedir/mtd-utils.git;tag=1234567890123456789012345678901234567890;protocol=http", 
        ("git://git.invalid.infradead.org/foo/mtd-utils.git;tag=1234567890123456789012345678901234567890", "git://.*/.*", "git://somewhere.org/somedir/BASENAME;protocol=http") 
            : "git://somewhere.org/somedir/mtd-utils.git;tag=1234567890123456789012345678901234567890;protocol=http", 
        ("git://git.invalid.infradead.org/foo/mtd-utils.git;tag=1234567890123456789012345678901234567890", "git://.*/.*", "git://somewhere.org/somedir/MIRRORNAME;protocol=http") 
            : "git://somewhere.org/somedir/git.invalid.infradead.org.foo.mtd-utils.git;tag=1234567890123456789012345678901234567890;protocol=http", 
        ("http://somewhere.org/somedir1/somedir2/somefile_1.2.3.tar.gz", "http://.*/.*", "http://somewhere2.org")
            : "http://somewhere2.org/somefile_1.2.3.tar.gz",
        ("http://somewhere.org/somedir1/somedir2/somefile_1.2.3.tar.gz", "http://.*/.*", "http://somewhere2.org/")
            : "http://somewhere2.org/somefile_1.2.3.tar.gz",
        ("git://someserver.org/bitbake;tag=1234567890123456789012345678901234567890;branch=master", "git://someserver.org/bitbake;branch=master", "git://git.openembedded.org/bitbake;protocol=http")
            : "git://git.openembedded.org/bitbake;tag=1234567890123456789012345678901234567890;branch=master;protocol=http",

        #Renaming files doesn't work
        #("http://somewhere.org/somedir1/somefile_1.2.3.tar.gz", "http://somewhere.org/somedir1/somefile_1.2.3.tar.gz", "http://somewhere2.org/somedir3/somefile_2.3.4.tar.gz") : "http://somewhere2.org/somedir3/somefile_2.3.4.tar.gz"
        #("file://sstate-xyz.tgz", "file://.*/.*", "file:///somewhere/1234/sstate-cache") : "file:///somewhere/1234/sstate-cache/sstate-xyz.tgz",
    }

    mirrorvar = "http://.*/.* file:///somepath/downloads/ \n" \
                "git://someserver.org/bitbake git://git.openembedded.org/bitbake \n" \
                "https://.*/.* file:///someotherpath/downloads/ \n" \
                "http://.*/.* file:///someotherpath/downloads/ \n"

    def test_urireplace(self):
        for k, v in self.replaceuris.items():
            ud = bb.fetch.FetchData(k[0], self.d)
            ud.setup_localpath(self.d)
            mirrors = bb.fetch2.mirror_from_string("%s %s" % (k[1], k[2]))
            newuris, uds = bb.fetch2.build_mirroruris(ud, mirrors, self.d)
            self.assertEqual([v], newuris)

    def test_urilist1(self):
        fetcher = bb.fetch.FetchData("http://downloads.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz", self.d)
        mirrors = bb.fetch2.mirror_from_string(self.mirrorvar)
        uris, uds = bb.fetch2.build_mirroruris(fetcher, mirrors, self.d)
        self.assertEqual(uris, ['file:///somepath/downloads/bitbake-1.0.tar.gz', 'file:///someotherpath/downloads/bitbake-1.0.tar.gz'])

    def test_urilist2(self):
        # Catch https:// -> files:// bug
        fetcher = bb.fetch.FetchData("https://downloads.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz", self.d)
        mirrors = bb.fetch2.mirror_from_string(self.mirrorvar)
        uris, uds = bb.fetch2.build_mirroruris(fetcher, mirrors, self.d)
        self.assertEqual(uris, ['file:///someotherpath/downloads/bitbake-1.0.tar.gz'])

    def test_mirror_of_mirror(self):
        # Test if mirror of a mirror works
        mirrorvar = self.mirrorvar + " http://.*/.* http://otherdownloads.yoctoproject.org/downloads/ \n"
        mirrorvar = mirrorvar + " http://otherdownloads.yoctoproject.org/.* http://downloads2.yoctoproject.org/downloads/ \n"
        fetcher = bb.fetch.FetchData("http://downloads.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz", self.d)
        mirrors = bb.fetch2.mirror_from_string(mirrorvar)
        uris, uds = bb.fetch2.build_mirroruris(fetcher, mirrors, self.d)
        self.assertEqual(uris, ['file:///somepath/downloads/bitbake-1.0.tar.gz', 
                                'file:///someotherpath/downloads/bitbake-1.0.tar.gz', 
                                'http://otherdownloads.yoctoproject.org/downloads/bitbake-1.0.tar.gz',
                                'http://downloads2.yoctoproject.org/downloads/bitbake-1.0.tar.gz'])

    recmirrorvar = "https://.*/[^/]*    http://AAAA/A/A/A/ \n" \
                   "https://.*/[^/]*    https://BBBB/B/B/B/ \n"

    def test_recursive(self):
        fetcher = bb.fetch.FetchData("https://downloads.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz", self.d)
        mirrors = bb.fetch2.mirror_from_string(self.recmirrorvar)
        uris, uds = bb.fetch2.build_mirroruris(fetcher, mirrors, self.d)
        self.assertEqual(uris, ['http://AAAA/A/A/A/bitbake/bitbake-1.0.tar.gz',
                                'https://BBBB/B/B/B/bitbake/bitbake-1.0.tar.gz',
                                'http://AAAA/A/A/A/B/B/bitbake/bitbake-1.0.tar.gz'])


class GitDownloadDirectoryNamingTest(FetcherTest):
    def setUp(self):
        super(GitDownloadDirectoryNamingTest, self).setUp()
        self.recipe_url = "git://git.openembedded.org/bitbake"
        self.recipe_dir = "git.openembedded.org.bitbake"
        self.mirror_url = "git://github.com/openembedded/bitbake.git;protocol=https"
        self.mirror_dir = "github.com.openembedded.bitbake.git"

        self.d.setVar('SRCREV', '82ea737a0b42a8b53e11c9cde141e9e9c0bd8c40')

    def setup_mirror_rewrite(self):
        self.d.setVar("PREMIRRORS", self.recipe_url + " " + self.mirror_url + " \n")

    @skipIfNoNetwork()
    def test_that_directory_is_named_after_recipe_url_when_no_mirroring_is_used(self):
        self.setup_mirror_rewrite()
        fetcher = bb.fetch.Fetch([self.recipe_url], self.d)

        fetcher.download()

        dir = os.listdir(self.dldir + "/git2")
        self.assertIn(self.recipe_dir, dir)

    @skipIfNoNetwork()
    def test_that_directory_exists_for_mirrored_url_and_recipe_url_when_mirroring_is_used(self):
        self.setup_mirror_rewrite()
        fetcher = bb.fetch.Fetch([self.recipe_url], self.d)

        fetcher.download()

        dir = os.listdir(self.dldir + "/git2")
        self.assertIn(self.mirror_dir, dir)
        self.assertIn(self.recipe_dir, dir)

    @skipIfNoNetwork()
    def test_that_recipe_directory_and_mirrored_directory_exists_when_mirroring_is_used_and_the_mirrored_directory_already_exists(self):
        self.setup_mirror_rewrite()
        fetcher = bb.fetch.Fetch([self.mirror_url], self.d)
        fetcher.download()
        fetcher = bb.fetch.Fetch([self.recipe_url], self.d)

        fetcher.download()

        dir = os.listdir(self.dldir + "/git2")
        self.assertIn(self.mirror_dir, dir)
        self.assertIn(self.recipe_dir, dir)


class TarballNamingTest(FetcherTest):
    def setUp(self):
        super(TarballNamingTest, self).setUp()
        self.recipe_url = "git://git.openembedded.org/bitbake"
        self.recipe_tarball = "git2_git.openembedded.org.bitbake.tar.gz"
        self.mirror_url = "git://github.com/openembedded/bitbake.git;protocol=https"
        self.mirror_tarball = "git2_github.com.openembedded.bitbake.git.tar.gz"

        self.d.setVar('BB_GENERATE_MIRROR_TARBALLS', '1')
        self.d.setVar('SRCREV', '82ea737a0b42a8b53e11c9cde141e9e9c0bd8c40')

    def setup_mirror_rewrite(self):
        self.d.setVar("PREMIRRORS", self.recipe_url + " " + self.mirror_url + " \n")

    @skipIfNoNetwork()
    def test_that_the_recipe_tarball_is_created_when_no_mirroring_is_used(self):
        fetcher = bb.fetch.Fetch([self.recipe_url], self.d)

        fetcher.download()

        dir = os.listdir(self.dldir)
        self.assertIn(self.recipe_tarball, dir)

    @skipIfNoNetwork()
    def test_that_the_mirror_tarball_is_created_when_mirroring_is_used(self):
        self.setup_mirror_rewrite()
        fetcher = bb.fetch.Fetch([self.recipe_url], self.d)

        fetcher.download()

        dir = os.listdir(self.dldir)
        self.assertIn(self.mirror_tarball, dir)


class GitShallowTarballNamingTest(FetcherTest):
    def setUp(self):
        super(GitShallowTarballNamingTest, self).setUp()
        self.recipe_url = "git://git.openembedded.org/bitbake"
        self.recipe_tarball = "gitshallow_git.openembedded.org.bitbake_82ea737-1_master.tar.gz"
        self.mirror_url = "git://github.com/openembedded/bitbake.git;protocol=https"
        self.mirror_tarball = "gitshallow_github.com.openembedded.bitbake.git_82ea737-1_master.tar.gz"

        self.d.setVar('BB_GIT_SHALLOW', '1')
        self.d.setVar('BB_GENERATE_SHALLOW_TARBALLS', '1')
        self.d.setVar('SRCREV', '82ea737a0b42a8b53e11c9cde141e9e9c0bd8c40')

    def setup_mirror_rewrite(self):
        self.d.setVar("PREMIRRORS", self.recipe_url + " " + self.mirror_url + " \n")

    @skipIfNoNetwork()
    def test_that_the_tarball_is_named_after_recipe_url_when_no_mirroring_is_used(self):
        fetcher = bb.fetch.Fetch([self.recipe_url], self.d)

        fetcher.download()

        dir = os.listdir(self.dldir)
        self.assertIn(self.recipe_tarball, dir)

    @skipIfNoNetwork()
    def test_that_the_mirror_tarball_is_created_when_mirroring_is_used(self):
        self.setup_mirror_rewrite()
        fetcher = bb.fetch.Fetch([self.recipe_url], self.d)

        fetcher.download()

        dir = os.listdir(self.dldir)
        self.assertIn(self.mirror_tarball, dir)


class FetcherLocalTest(FetcherTest):
    def setUp(self):
        def touch(fn):
            with open(fn, 'a'):
                os.utime(fn, None)

        super(FetcherLocalTest, self).setUp()
        self.localsrcdir = os.path.join(self.tempdir, 'localsrc')
        os.makedirs(self.localsrcdir)
        touch(os.path.join(self.localsrcdir, 'a'))
        touch(os.path.join(self.localsrcdir, 'b'))
        os.makedirs(os.path.join(self.localsrcdir, 'dir'))
        touch(os.path.join(self.localsrcdir, 'dir', 'c'))
        touch(os.path.join(self.localsrcdir, 'dir', 'd'))
        os.makedirs(os.path.join(self.localsrcdir, 'dir', 'subdir'))
        touch(os.path.join(self.localsrcdir, 'dir', 'subdir', 'e'))
        self.d.setVar("FILESPATH", self.localsrcdir)

    def fetchUnpack(self, uris):
        fetcher = bb.fetch.Fetch(uris, self.d)
        fetcher.download()
        fetcher.unpack(self.unpackdir)
        flst = []
        for root, dirs, files in os.walk(self.unpackdir):
            for f in files:
                flst.append(os.path.relpath(os.path.join(root, f), self.unpackdir))
        flst.sort()
        return flst

    def test_local(self):
        tree = self.fetchUnpack(['file://a', 'file://dir/c'])
        self.assertEqual(tree, ['a', 'dir/c'])

    def test_local_wildcard(self):
        tree = self.fetchUnpack(['file://a', 'file://dir/*'])
        self.assertEqual(tree, ['a',  'dir/c', 'dir/d', 'dir/subdir/e'])

    def test_local_dir(self):
        tree = self.fetchUnpack(['file://a', 'file://dir'])
        self.assertEqual(tree, ['a', 'dir/c', 'dir/d', 'dir/subdir/e'])

    def test_local_subdir(self):
        tree = self.fetchUnpack(['file://dir/subdir'])
        self.assertEqual(tree, ['dir/subdir/e'])

    def test_local_subdir_file(self):
        tree = self.fetchUnpack(['file://dir/subdir/e'])
        self.assertEqual(tree, ['dir/subdir/e'])

    def test_local_subdirparam(self):
        tree = self.fetchUnpack(['file://a;subdir=bar', 'file://dir;subdir=foo/moo'])
        self.assertEqual(tree, ['bar/a', 'foo/moo/dir/c', 'foo/moo/dir/d', 'foo/moo/dir/subdir/e'])

    def test_local_deepsubdirparam(self):
        tree = self.fetchUnpack(['file://dir/subdir/e;subdir=bar'])
        self.assertEqual(tree, ['bar/dir/subdir/e'])

    def test_local_absolutedir(self):
        # Unpacking to an absolute path that is a subdirectory of the root
        # should work
        tree = self.fetchUnpack(['file://a;subdir=%s' % os.path.join(self.unpackdir, 'bar')])

        # Unpacking to an absolute path outside of the root should fail
        with self.assertRaises(bb.fetch2.UnpackError):
            self.fetchUnpack(['file://a;subdir=/bin/sh'])

class FetcherNoNetworkTest(FetcherTest):
    def setUp(self):
        super().setUp()
        # all test cases are based on not having network
        self.d.setVar("BB_NO_NETWORK", "1")

    def test_missing(self):
        string = "this is a test file\n".encode("utf-8")
        self.d.setVarFlag("SRC_URI", "md5sum", hashlib.md5(string).hexdigest())
        self.d.setVarFlag("SRC_URI", "sha256sum", hashlib.sha256(string).hexdigest())

        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz")))
        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz.done")))
        fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/test-file.tar.gz"], self.d)
        with self.assertRaises(bb.fetch2.NetworkAccess):
            fetcher.download()

    def test_valid_missing_donestamp(self):
        # create the file in the download directory with correct hash
        string = "this is a test file\n".encode("utf-8")
        with open(os.path.join(self.dldir, "test-file.tar.gz"), "wb") as f:
            f.write(string)

        self.d.setVarFlag("SRC_URI", "md5sum", hashlib.md5(string).hexdigest())
        self.d.setVarFlag("SRC_URI", "sha256sum", hashlib.sha256(string).hexdigest())

        self.assertTrue(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz")))
        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz.done")))
        fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/test-file.tar.gz"], self.d)
        fetcher.download()
        self.assertTrue(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz.done")))

    def test_invalid_missing_donestamp(self):
        # create an invalid file in the download directory with incorrect hash
        string = "this is a test file\n".encode("utf-8")
        with open(os.path.join(self.dldir, "test-file.tar.gz"), "wb"):
            pass

        self.d.setVarFlag("SRC_URI", "md5sum", hashlib.md5(string).hexdigest())
        self.d.setVarFlag("SRC_URI", "sha256sum", hashlib.sha256(string).hexdigest())

        self.assertTrue(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz")))
        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz.done")))
        fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/test-file.tar.gz"], self.d)
        with self.assertRaises(bb.fetch2.NetworkAccess):
            fetcher.download()
        # the existing file should not exist or should have be moved to "bad-checksum"
        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz")))

    def test_nochecksums_missing(self):
        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz")))
        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz.done")))
        # ssh fetch does not support checksums
        fetcher = bb.fetch.Fetch(["ssh://invalid@invalid.yoctoproject.org/test-file.tar.gz"], self.d)
        # attempts to download with missing donestamp
        with self.assertRaises(bb.fetch2.NetworkAccess):
            fetcher.download()

    def test_nochecksums_missing_donestamp(self):
        # create a file in the download directory
        with open(os.path.join(self.dldir, "test-file.tar.gz"), "wb"):
            pass

        self.assertTrue(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz")))
        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz.done")))
        # ssh fetch does not support checksums
        fetcher = bb.fetch.Fetch(["ssh://invalid@invalid.yoctoproject.org/test-file.tar.gz"], self.d)
        # attempts to download with missing donestamp
        with self.assertRaises(bb.fetch2.NetworkAccess):
            fetcher.download()

    def test_nochecksums_has_donestamp(self):
        # create a file in the download directory with the donestamp
        with open(os.path.join(self.dldir, "test-file.tar.gz"), "wb"):
            pass
        with open(os.path.join(self.dldir, "test-file.tar.gz.done"), "wb"):
            pass

        self.assertTrue(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz")))
        self.assertTrue(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz.done")))
        # ssh fetch does not support checksums
        fetcher = bb.fetch.Fetch(["ssh://invalid@invalid.yoctoproject.org/test-file.tar.gz"], self.d)
        # should not fetch
        fetcher.download()
        # both files should still exist
        self.assertTrue(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz")))
        self.assertTrue(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz.done")))

    def test_nochecksums_missing_has_donestamp(self):
        # create a file in the download directory with the donestamp
        with open(os.path.join(self.dldir, "test-file.tar.gz.done"), "wb"):
            pass

        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz")))
        self.assertTrue(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz.done")))
        # ssh fetch does not support checksums
        fetcher = bb.fetch.Fetch(["ssh://invalid@invalid.yoctoproject.org/test-file.tar.gz"], self.d)
        with self.assertRaises(bb.fetch2.NetworkAccess):
            fetcher.download()
        # both files should still exist
        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz")))
        self.assertFalse(os.path.exists(os.path.join(self.dldir, "test-file.tar.gz.done")))

class FetcherNetworkTest(FetcherTest):
    @skipIfNoNetwork()
    def test_fetch(self):
        fetcher = bb.fetch.Fetch(["http://downloads.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz", "http://downloads.yoctoproject.org/releases/bitbake/bitbake-1.1.tar.gz"], self.d)
        fetcher.download()
        self.assertEqual(os.path.getsize(self.dldir + "/bitbake-1.0.tar.gz"), 57749)
        self.assertEqual(os.path.getsize(self.dldir + "/bitbake-1.1.tar.gz"), 57892)
        self.d.setVar("BB_NO_NETWORK", "1")
        fetcher = bb.fetch.Fetch(["http://downloads.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz", "http://downloads.yoctoproject.org/releases/bitbake/bitbake-1.1.tar.gz"], self.d)
        fetcher.download()
        fetcher.unpack(self.unpackdir)
        self.assertEqual(len(os.listdir(self.unpackdir + "/bitbake-1.0/")), 9)
        self.assertEqual(len(os.listdir(self.unpackdir + "/bitbake-1.1/")), 9)

    @skipIfNoNetwork()
    def test_fetch_mirror(self):
        self.d.setVar("MIRRORS", "http://.*/.* http://downloads.yoctoproject.org/releases/bitbake")
        fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz"], self.d)
        fetcher.download()
        self.assertEqual(os.path.getsize(self.dldir + "/bitbake-1.0.tar.gz"), 57749)

    @skipIfNoNetwork()
    def test_fetch_mirror_of_mirror(self):
        self.d.setVar("MIRRORS", "http://.*/.* http://invalid2.yoctoproject.org/ \n http://invalid2.yoctoproject.org/.* http://downloads.yoctoproject.org/releases/bitbake")
        fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz"], self.d)
        fetcher.download()
        self.assertEqual(os.path.getsize(self.dldir + "/bitbake-1.0.tar.gz"), 57749)

    @skipIfNoNetwork()
    def test_fetch_file_mirror_of_mirror(self):
        self.d.setVar("MIRRORS", "http://.*/.* file:///some1where/ \n file:///some1where/.* file://some2where/ \n file://some2where/.* http://downloads.yoctoproject.org/releases/bitbake")
        fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz"], self.d)
        os.mkdir(self.dldir + "/some2where")
        fetcher.download()
        self.assertEqual(os.path.getsize(self.dldir + "/bitbake-1.0.tar.gz"), 57749)

    @skipIfNoNetwork()
    def test_fetch_premirror(self):
        self.d.setVar("PREMIRRORS", "http://.*/.* http://downloads.yoctoproject.org/releases/bitbake")
        fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz"], self.d)
        fetcher.download()
        self.assertEqual(os.path.getsize(self.dldir + "/bitbake-1.0.tar.gz"), 57749)

    @skipIfNoNetwork()
    def gitfetcher(self, url1, url2):
        def checkrevision(self, fetcher):
            fetcher.unpack(self.unpackdir)
            revision = bb.process.run("git rev-parse HEAD", shell=True, cwd=self.unpackdir + "/git")[0].strip()
            self.assertEqual(revision, "270a05b0b4ba0959fe0624d2a4885d7b70426da5")

        self.d.setVar("BB_GENERATE_MIRROR_TARBALLS", "1")
        self.d.setVar("SRCREV", "270a05b0b4ba0959fe0624d2a4885d7b70426da5")
        fetcher = bb.fetch.Fetch([url1], self.d)
        fetcher.download()
        checkrevision(self, fetcher)
        # Wipe out the dldir clone and the unpacked source, turn off the network and check mirror tarball works
        bb.utils.prunedir(self.dldir + "/git2/")
        bb.utils.prunedir(self.unpackdir)
        self.d.setVar("BB_NO_NETWORK", "1")
        fetcher = bb.fetch.Fetch([url2], self.d)
        fetcher.download()
        checkrevision(self, fetcher)

    @skipIfNoNetwork()
    def test_gitfetch(self):
        url1 = url2 = "git://git.openembedded.org/bitbake"
        self.gitfetcher(url1, url2)

    @skipIfNoNetwork()
    def test_gitfetch_goodsrcrev(self):
        # SRCREV is set but matches rev= parameter
        url1 = url2 = "git://git.openembedded.org/bitbake;rev=270a05b0b4ba0959fe0624d2a4885d7b70426da5"
        self.gitfetcher(url1, url2)

    @skipIfNoNetwork()
    def test_gitfetch_badsrcrev(self):
        # SRCREV is set but does not match rev= parameter
        url1 = url2 = "git://git.openembedded.org/bitbake;rev=dead05b0b4ba0959fe0624d2a4885d7b70426da5"
        self.assertRaises(bb.fetch.FetchError, self.gitfetcher, url1, url2)

    @skipIfNoNetwork()
    def test_gitfetch_tagandrev(self):
        # SRCREV is set but does not match rev= parameter
        url1 = url2 = "git://git.openembedded.org/bitbake;rev=270a05b0b4ba0959fe0624d2a4885d7b70426da5;tag=270a05b0b4ba0959fe0624d2a4885d7b70426da5"
        self.assertRaises(bb.fetch.FetchError, self.gitfetcher, url1, url2)

    @skipIfNoNetwork()
    def test_gitfetch_localusehead(self):
        # Create dummy local Git repo
        src_dir = tempfile.mkdtemp(dir=self.tempdir,
                                   prefix='gitfetch_localusehead_')
        src_dir = os.path.abspath(src_dir)
        bb.process.run("git init", cwd=src_dir)
        bb.process.run("git config user.email 'you@example.com'", cwd=src_dir)
        bb.process.run("git config user.name 'Your Name'", cwd=src_dir)
        bb.process.run("git commit --allow-empty -m'Dummy commit'",
                       cwd=src_dir)
        # Use other branch than master
        bb.process.run("git checkout -b my-devel", cwd=src_dir)
        bb.process.run("git commit --allow-empty -m'Dummy commit 2'",
                       cwd=src_dir)
        stdout = bb.process.run("git rev-parse HEAD", cwd=src_dir)
        orig_rev = stdout[0].strip()

        # Fetch and check revision
        self.d.setVar("SRCREV", "AUTOINC")
        url = "git://" + src_dir + ";protocol=file;usehead=1"
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        fetcher.unpack(self.unpackdir)
        stdout = bb.process.run("git rev-parse HEAD",
                                cwd=os.path.join(self.unpackdir, 'git'))
        unpack_rev = stdout[0].strip()
        self.assertEqual(orig_rev, unpack_rev)

    @skipIfNoNetwork()
    def test_gitfetch_remoteusehead(self):
        url = "git://git.openembedded.org/bitbake;usehead=1"
        self.assertRaises(bb.fetch.ParameterError, self.gitfetcher, url, url)

    @skipIfNoNetwork()
    def test_gitfetch_finds_local_tarball_for_mirrored_url_when_previous_downloaded_by_the_recipe_url(self):
        recipeurl = "git://git.openembedded.org/bitbake"
        mirrorurl = "git://someserver.org/bitbake"
        self.d.setVar("PREMIRRORS", "git://someserver.org/bitbake git://git.openembedded.org/bitbake \n")
        self.gitfetcher(recipeurl, mirrorurl)

    @skipIfNoNetwork()
    def test_gitfetch_finds_local_tarball_when_previous_downloaded_from_a_premirror(self):
        recipeurl = "git://someserver.org/bitbake"
        self.d.setVar("PREMIRRORS", "git://someserver.org/bitbake git://git.openembedded.org/bitbake \n")
        self.gitfetcher(recipeurl, recipeurl)

    @skipIfNoNetwork()
    def test_gitfetch_finds_local_repository_when_premirror_rewrites_the_recipe_url(self):
        realurl = "git://git.openembedded.org/bitbake"
        recipeurl = "git://someserver.org/bitbake"
        self.sourcedir = self.unpackdir.replace("unpacked", "sourcemirror.git")
        os.chdir(self.tempdir)
        bb.process.run("git clone %s %s 2> /dev/null" % (realurl, self.sourcedir), shell=True)
        self.d.setVar("PREMIRRORS", "%s git://%s;protocol=file \n" % (recipeurl, self.sourcedir))
        self.gitfetcher(recipeurl, recipeurl)

    @skipIfNoNetwork()
    def test_git_submodule(self):
        # URL with ssh submodules
        url = "gitsm://git.yoctoproject.org/git-submodule-test;branch=ssh-gitsm-tests;rev=049da4a6cb198d7c0302e9e8b243a1443cb809a7"
        # Original URL (comment this if you have ssh access to git.yoctoproject.org)
        url = "gitsm://git.yoctoproject.org/git-submodule-test;branch=master;rev=a2885dd7d25380d23627e7544b7bbb55014b16ee"
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        # Previous cwd has been deleted
        os.chdir(os.path.dirname(self.unpackdir))
        fetcher.unpack(self.unpackdir)

        repo_path = os.path.join(self.tempdir, 'unpacked', 'git')
        self.assertTrue(os.path.exists(repo_path), msg='Unpacked repository missing')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'bitbake')), msg='bitbake submodule missing')
        self.assertFalse(os.path.exists(os.path.join(repo_path, 'na')), msg='uninitialized submodule present')

        # Only when we're running the extended test with a submodule's submodule, can we check this.
        if os.path.exists(os.path.join(repo_path, 'bitbake-gitsm-test1')):
            self.assertTrue(os.path.exists(os.path.join(repo_path, 'bitbake-gitsm-test1', 'bitbake')), msg='submodule of submodule missing')

    @skipIfNoNetwork()
    def test_git_submodule_dbus_broker(self):
        # The following external repositories have show failures in fetch and unpack operations
        # We want to avoid regressions!
        url = "gitsm://github.com/bus1/dbus-broker;protocol=https;rev=fc874afa0992d0c75ec25acb43d344679f0ee7d2;branch=main"
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        # Previous cwd has been deleted
        os.chdir(os.path.dirname(self.unpackdir))
        fetcher.unpack(self.unpackdir)

        repo_path = os.path.join(self.tempdir, 'unpacked', 'git')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/subprojects/c-dvar/config')), msg='Missing submodule config "subprojects/c-dvar"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/subprojects/c-list/config')), msg='Missing submodule config "subprojects/c-list"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/subprojects/c-rbtree/config')), msg='Missing submodule config "subprojects/c-rbtree"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/subprojects/c-sundry/config')), msg='Missing submodule config "subprojects/c-sundry"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/subprojects/c-utf8/config')), msg='Missing submodule config "subprojects/c-utf8"')

    @skipIfNoNetwork()
    def test_git_submodule_CLI11(self):
        url = "gitsm://github.com/CLIUtils/CLI11;protocol=https;rev=bd4dc911847d0cde7a6b41dfa626a85aab213baf;branch=main"
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        # Previous cwd has been deleted
        os.chdir(os.path.dirname(self.unpackdir))
        fetcher.unpack(self.unpackdir)

        repo_path = os.path.join(self.tempdir, 'unpacked', 'git')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/extern/googletest/config')), msg='Missing submodule config "extern/googletest"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/extern/json/config')), msg='Missing submodule config "extern/json"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/extern/sanitizers/config')), msg='Missing submodule config "extern/sanitizers"')

    @skipIfNoNetwork()
    def test_git_submodule_update_CLI11(self):
        """ Prevent regression on update detection not finding missing submodule, or modules without needed commits """
        url = "gitsm://github.com/CLIUtils/CLI11;protocol=https;rev=cf6a99fa69aaefe477cc52e3ef4a7d2d7fa40714;branch=main"
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()

        # CLI11 that pulls in a newer nlohmann-json
        url = "gitsm://github.com/CLIUtils/CLI11;protocol=https;rev=49ac989a9527ee9bb496de9ded7b4872c2e0e5ca;branch=main"
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        # Previous cwd has been deleted
        os.chdir(os.path.dirname(self.unpackdir))
        fetcher.unpack(self.unpackdir)

        repo_path = os.path.join(self.tempdir, 'unpacked', 'git')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/extern/googletest/config')), msg='Missing submodule config "extern/googletest"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/extern/json/config')), msg='Missing submodule config "extern/json"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/extern/sanitizers/config')), msg='Missing submodule config "extern/sanitizers"')

    @skipIfNoNetwork()
    def test_git_submodule_aktualizr(self):
        url = "gitsm://github.com/advancedtelematic/aktualizr;branch=master;protocol=https;rev=d00d1a04cc2366d1a5f143b84b9f507f8bd32c44"
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        # Previous cwd has been deleted
        os.chdir(os.path.dirname(self.unpackdir))
        fetcher.unpack(self.unpackdir)

        repo_path = os.path.join(self.tempdir, 'unpacked', 'git')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/partial/extern/isotp-c/config')), msg='Missing submodule config "partial/extern/isotp-c/config"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/partial/extern/isotp-c/modules/deps/bitfield-c/config')), msg='Missing submodule config "partial/extern/isotp-c/modules/deps/bitfield-c/config"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'partial/extern/isotp-c/deps/bitfield-c/.git')), msg="Submodule of submodule isotp-c did not unpack properly")
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/tests/tuf-test-vectors/config')), msg='Missing submodule config "tests/tuf-test-vectors/config"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/third_party/googletest/config')), msg='Missing submodule config "third_party/googletest/config"')
        self.assertTrue(os.path.exists(os.path.join(repo_path, '.git/modules/third_party/HdrHistogram_c/config')), msg='Missing submodule config "third_party/HdrHistogram_c/config"')

    @skipIfNoNetwork()
    def test_git_submodule_iotedge(self):
        """ Prevent regression on deeply nested submodules not being checked out properly, even though they were fetched. """

        # This repository also has submodules where the module (name), path and url do not align
        url = "gitsm://github.com/azure/iotedge.git;protocol=https;rev=d76e0316c6f324345d77c48a83ce836d09392699"
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        # Previous cwd has been deleted
        os.chdir(os.path.dirname(self.unpackdir))
        fetcher.unpack(self.unpackdir)

        repo_path = os.path.join(self.tempdir, 'unpacked', 'git')

        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/c-shared/README.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/c-shared/testtools/ctest/README.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/c-shared/testtools/testrunner/readme.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/c-shared/testtools/umock-c/readme.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/c-shared/testtools/umock-c/deps/ctest/README.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/c-shared/testtools/umock-c/deps/testrunner/readme.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/utpm/README.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/utpm/deps/c-utility/README.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/utpm/deps/c-utility/testtools/ctest/README.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/utpm/deps/c-utility/testtools/testrunner/readme.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/utpm/deps/c-utility/testtools/umock-c/readme.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/utpm/deps/c-utility/testtools/umock-c/deps/ctest/README.md')), msg='Missing submodule checkout')
        self.assertTrue(os.path.exists(os.path.join(repo_path, 'edgelet/hsm-sys/azure-iot-hsm-c/deps/utpm/deps/c-utility/testtools/umock-c/deps/testrunner/readme.md')), msg='Missing submodule checkout')

class SVNTest(FetcherTest):
    def skipIfNoSvn():
        import shutil
        if not shutil.which("svn"):
            return unittest.skip("svn not installed,  tests being skipped")

        if not shutil.which("svnadmin"):
            return unittest.skip("svnadmin not installed,  tests being skipped")

        return lambda f: f

    @skipIfNoSvn()
    def setUp(self):
        """ Create a local repository """

        super(SVNTest, self).setUp()

        # Create something we can fetch
        src_dir = tempfile.mkdtemp(dir=self.tempdir,
                                   prefix='svnfetch_srcdir_')
        src_dir = os.path.abspath(src_dir)
        bb.process.run("echo readme > README.md", cwd=src_dir)

        # Store it in a local SVN repository
        repo_dir = tempfile.mkdtemp(dir=self.tempdir,
                                   prefix='svnfetch_localrepo_')
        repo_dir = os.path.abspath(repo_dir)
        bb.process.run("svnadmin create project", cwd=repo_dir)

        self.repo_url = "file://%s/project" % repo_dir
        bb.process.run("svn import --non-interactive -m 'Initial import' %s %s/trunk" % (src_dir, self.repo_url),
                       cwd=repo_dir)

        bb.process.run("svn co %s svnfetch_co" % self.repo_url, cwd=self.tempdir)
        # Github will emulate SVN.  Use this to check if we're downloding...
        bb.process.run("svn propset svn:externals 'bitbake https://github.com/PhilipHazel/pcre2.git' .",
                       cwd=os.path.join(self.tempdir, 'svnfetch_co', 'trunk'))
        bb.process.run("svn commit --non-interactive -m 'Add external'",
                       cwd=os.path.join(self.tempdir, 'svnfetch_co', 'trunk'))

        self.src_dir = src_dir
        self.repo_dir = repo_dir

    @skipIfNoSvn()
    def tearDown(self):
        os.chdir(self.origdir)
        if os.environ.get("BB_TMPDIR_NOCLEAN") == "yes":
            print("Not cleaning up %s. Please remove manually." % self.tempdir)
        else:
            bb.utils.prunedir(self.tempdir)

    @skipIfNoSvn()
    @skipIfNoNetwork()
    def test_noexternal_svn(self):
        # Always match the rev count from setUp (currently rev 2)
        url = "svn://%s;module=trunk;protocol=file;rev=2" % self.repo_url.replace('file://', '')
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        os.chdir(os.path.dirname(self.unpackdir))
        fetcher.unpack(self.unpackdir)

        self.assertTrue(os.path.exists(os.path.join(self.unpackdir, 'trunk')), msg="Missing trunk")
        self.assertTrue(os.path.exists(os.path.join(self.unpackdir, 'trunk', 'README.md')), msg="Missing contents")
        self.assertFalse(os.path.exists(os.path.join(self.unpackdir, 'trunk/bitbake/trunk')), msg="External dir should NOT exist")
        self.assertFalse(os.path.exists(os.path.join(self.unpackdir, 'trunk/bitbake/trunk', 'README')), msg="External README should NOT exit")

    @skipIfNoSvn()
    def test_external_svn(self):
        # Always match the rev count from setUp (currently rev 2)
        url = "svn://%s;module=trunk;protocol=file;externals=allowed;rev=2" % self.repo_url.replace('file://', '')
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        os.chdir(os.path.dirname(self.unpackdir))
        fetcher.unpack(self.unpackdir)

        self.assertTrue(os.path.exists(os.path.join(self.unpackdir, 'trunk')), msg="Missing trunk")
        self.assertTrue(os.path.exists(os.path.join(self.unpackdir, 'trunk', 'README.md')), msg="Missing contents")
        self.assertTrue(os.path.exists(os.path.join(self.unpackdir, 'trunk/bitbake/trunk')), msg="External dir should exist")
        self.assertTrue(os.path.exists(os.path.join(self.unpackdir, 'trunk/bitbake/trunk', 'README')), msg="External README should exit")

class TrustedNetworksTest(FetcherTest):
    def test_trusted_network(self):
        # Ensure trusted_network returns False when the host IS in the list.
        url = "git://Someserver.org/foo;rev=1"
        self.d.setVar("BB_ALLOWED_NETWORKS", "server1.org someserver.org server2.org server3.org")
        self.assertTrue(bb.fetch.trusted_network(self.d, url))

    def test_wild_trusted_network(self):
        # Ensure trusted_network returns true when the *.host IS in the list.
        url = "git://Someserver.org/foo;rev=1"
        self.d.setVar("BB_ALLOWED_NETWORKS", "server1.org *.someserver.org server2.org server3.org")
        self.assertTrue(bb.fetch.trusted_network(self.d, url))

    def test_prefix_wild_trusted_network(self):
        # Ensure trusted_network returns true when the prefix matches *.host.
        url = "git://git.Someserver.org/foo;rev=1"
        self.d.setVar("BB_ALLOWED_NETWORKS", "server1.org *.someserver.org server2.org server3.org")
        self.assertTrue(bb.fetch.trusted_network(self.d, url))

    def test_two_prefix_wild_trusted_network(self):
        # Ensure trusted_network returns true when the prefix matches *.host.
        url = "git://something.git.Someserver.org/foo;rev=1"
        self.d.setVar("BB_ALLOWED_NETWORKS", "server1.org *.someserver.org server2.org server3.org")
        self.assertTrue(bb.fetch.trusted_network(self.d, url))

    def test_port_trusted_network(self):
        # Ensure trusted_network returns True, even if the url specifies a port.
        url = "git://someserver.org:8080/foo;rev=1"
        self.d.setVar("BB_ALLOWED_NETWORKS", "someserver.org")
        self.assertTrue(bb.fetch.trusted_network(self.d, url))

    def test_untrusted_network(self):
        # Ensure trusted_network returns False when the host is NOT in the list.
        url = "git://someserver.org/foo;rev=1"
        self.d.setVar("BB_ALLOWED_NETWORKS", "server1.org server2.org server3.org")
        self.assertFalse(bb.fetch.trusted_network(self.d, url))

    def test_wild_untrusted_network(self):
        # Ensure trusted_network returns False when the host is NOT in the list.
        url = "git://*.someserver.org/foo;rev=1"
        self.d.setVar("BB_ALLOWED_NETWORKS", "server1.org server2.org server3.org")
        self.assertFalse(bb.fetch.trusted_network(self.d, url))

class URLHandle(unittest.TestCase):

    datatable = {
       "http://www.google.com/index.html" : ('http', 'www.google.com', '/index.html', '', '', {}),
       "cvs://anoncvs@cvs.handhelds.org/cvs;module=familiar/dist/ipkg" : ('cvs', 'cvs.handhelds.org', '/cvs', 'anoncvs', '', {'module': 'familiar/dist/ipkg'}),
       "cvs://anoncvs:anonymous@cvs.handhelds.org/cvs;tag=V0-99-81;module=familiar/dist/ipkg" : ('cvs', 'cvs.handhelds.org', '/cvs', 'anoncvs', 'anonymous', collections.OrderedDict([('tag', 'V0-99-81'), ('module', 'familiar/dist/ipkg')])),
       "git://git.openembedded.org/bitbake;branch=@foo" : ('git', 'git.openembedded.org', '/bitbake', '', '', {'branch': '@foo'}),
       "file://somelocation;someparam=1": ('file', '', 'somelocation', '', '', {'someparam': '1'}),
    }
    # we require a pathname to encodeurl but users can still pass such urls to 
    # decodeurl and we need to handle them
    decodedata = datatable.copy()
    decodedata.update({
       "http://somesite.net;someparam=1": ('http', 'somesite.net', '/', '', '', {'someparam': '1'}),
    })

    def test_decodeurl(self):
        for k, v in self.decodedata.items():
            result = bb.fetch.decodeurl(k)
            self.assertEqual(result, v)

    def test_encodeurl(self):
        for k, v in self.datatable.items():
            result = bb.fetch.encodeurl(v)
            self.assertEqual(result, k)

class FetchLatestVersionTest(FetcherTest):

    test_git_uris = {
        # version pattern "X.Y.Z"
        ("mx-1.0", "git://github.com/clutter-project/mx.git;branch=mx-1.4;protocol=https", "9b1db6b8060bd00b121a692f942404a24ae2960f", "")
            : "1.99.4",
        # version pattern "vX.Y"
        # mirror of git.infradead.org since network issues interfered with testing
        ("mtd-utils", "git://git.yoctoproject.org/mtd-utils.git", "ca39eb1d98e736109c64ff9c1aa2a6ecca222d8f", "")
            : "1.5.0",
        # version pattern "pkg_name-X.Y"
        # mirror of git://anongit.freedesktop.org/git/xorg/proto/presentproto since network issues interfered with testing
        ("presentproto", "git://git.yoctoproject.org/bbfetchtests-presentproto", "24f3a56e541b0a9e6c6ee76081f441221a120ef9", "")
            : "1.0",
        # version pattern "pkg_name-vX.Y.Z"
        ("dtc", "git://git.yoctoproject.org/bbfetchtests-dtc.git", "65cc4d2748a2c2e6f27f1cf39e07a5dbabd80ebf", "")
            : "1.4.0",
        # combination version pattern
        ("sysprof", "git://gitlab.gnome.org/GNOME/sysprof.git;protocol=https", "cd44ee6644c3641507fb53b8a2a69137f2971219", "")
            : "1.2.0",
        ("u-boot-mkimage", "git://git.denx.de/u-boot.git;branch=master;protocol=git", "62c175fbb8a0f9a926c88294ea9f7e88eb898f6c", "")
            : "2014.01",
        # version pattern "yyyymmdd"
        ("mobile-broadband-provider-info", "git://gitlab.gnome.org/GNOME/mobile-broadband-provider-info.git;protocol=https", "4ed19e11c2975105b71b956440acdb25d46a347d", "")
            : "20120614",
        # packages with a valid UPSTREAM_CHECK_GITTAGREGEX
                # mirror of git://anongit.freedesktop.org/xorg/driver/xf86-video-omap since network issues interfered with testing
        ("xf86-video-omap", "git://git.yoctoproject.org/bbfetchtests-xf86-video-omap", "ae0394e687f1a77e966cf72f895da91840dffb8f", r"(?P<pver>(\d+\.(\d\.?)*))")
            : "0.4.3",
        ("build-appliance-image", "git://git.yoctoproject.org/poky", "b37dd451a52622d5b570183a81583cc34c2ff555", r"(?P<pver>(([0-9][\.|_]?)+[0-9]))")
            : "11.0.0",
        ("chkconfig-alternatives-native", "git://github.com/kergoth/chkconfig;branch=sysroot;protocol=https", "cd437ecbd8986c894442f8fce1e0061e20f04dee", r"chkconfig\-(?P<pver>((\d+[\.\-_]*)+))")
            : "1.3.59",
        ("remake", "git://github.com/rocky/remake.git;protocol=https", "f05508e521987c8494c92d9c2871aec46307d51d", r"(?P<pver>(\d+\.(\d+\.)*\d*(\+dbg\d+(\.\d+)*)*))")
            : "3.82+dbg0.9",
    }

    test_wget_uris = {
        #
        # packages with versions inside directory name
        #
        # http://kernel.org/pub/linux/utils/util-linux/v2.23/util-linux-2.24.2.tar.bz2
        ("util-linux", "/pub/linux/utils/util-linux/v2.23/util-linux-2.24.2.tar.bz2", "", "")
            : "2.24.2",
        # http://www.abisource.com/downloads/enchant/1.6.0/enchant-1.6.0.tar.gz
        ("enchant", "/downloads/enchant/1.6.0/enchant-1.6.0.tar.gz", "", "")
            : "1.6.0",
        # http://www.cmake.org/files/v2.8/cmake-2.8.12.1.tar.gz
        ("cmake", "/files/v2.8/cmake-2.8.12.1.tar.gz", "", "")
            : "2.8.12.1",
        #
        # packages with versions only in current directory
        #
        # http://downloads.yoctoproject.org/releases/eglibc/eglibc-2.18-svnr23787.tar.bz2
        ("eglic", "/releases/eglibc/eglibc-2.18-svnr23787.tar.bz2", "", "")
            : "2.19",
        # http://downloads.yoctoproject.org/releases/gnu-config/gnu-config-20120814.tar.bz2
        ("gnu-config", "/releases/gnu-config/gnu-config-20120814.tar.bz2", "", "")
            : "20120814",
        #
        # packages with "99" in the name of possible version
        #
        # http://freedesktop.org/software/pulseaudio/releases/pulseaudio-4.0.tar.xz
        ("pulseaudio", "/software/pulseaudio/releases/pulseaudio-4.0.tar.xz", "", "")
            : "5.0",
        # http://xorg.freedesktop.org/releases/individual/xserver/xorg-server-1.15.1.tar.bz2
        ("xserver-xorg", "/releases/individual/xserver/xorg-server-1.15.1.tar.bz2", "", "")
            : "1.15.1",
        #
        # packages with valid UPSTREAM_CHECK_URI and UPSTREAM_CHECK_REGEX
        #
        # http://www.cups.org/software/1.7.2/cups-1.7.2-source.tar.bz2
        # https://github.com/apple/cups/releases
        ("cups", "/software/1.7.2/cups-1.7.2-source.tar.bz2", "/apple/cups/releases", r"(?P<name>cups\-)(?P<pver>((\d+[\.\-_]*)+))\-source\.tar\.gz")
            : "2.0.0",
        # http://download.oracle.com/berkeley-db/db-5.3.21.tar.gz
        # http://ftp.debian.org/debian/pool/main/d/db5.3/
        ("db", "/berkeley-db/db-5.3.21.tar.gz", "/debian/pool/main/d/db5.3/", r"(?P<name>db5\.3_)(?P<pver>\d+(\.\d+)+).+\.orig\.tar\.xz")
            : "5.3.10",
    }

    @skipIfNoNetwork()
    def test_git_latest_versionstring(self):
        for k, v in self.test_git_uris.items():
            self.d.setVar("PN", k[0])
            self.d.setVar("SRCREV", k[2])
            self.d.setVar("UPSTREAM_CHECK_GITTAGREGEX", k[3])
            ud = bb.fetch2.FetchData(k[1], self.d)
            pupver= ud.method.latest_versionstring(ud, self.d)
            verstring = pupver[0]
            self.assertTrue(verstring, msg="Could not find upstream version for %s" % k[0])
            r = bb.utils.vercmp_string(v, verstring)
            self.assertTrue(r == -1 or r == 0, msg="Package %s, version: %s <= %s" % (k[0], v, verstring))

    def test_wget_latest_versionstring(self):
        testdata = os.path.dirname(os.path.abspath(__file__)) + "/fetch-testdata"
        server = HTTPService(testdata)
        server.start()
        port = server.port
        try:
            for k, v in self.test_wget_uris.items():
                self.d.setVar("PN", k[0])
                checkuri = ""
                if k[2]:
                    checkuri = "http://localhost:%s/" % port + k[2]
                self.d.setVar("UPSTREAM_CHECK_URI", checkuri)
                self.d.setVar("UPSTREAM_CHECK_REGEX", k[3])
                url = "http://localhost:%s/" % port + k[1]
                ud = bb.fetch2.FetchData(url, self.d)
                pupver = ud.method.latest_versionstring(ud, self.d)
                verstring = pupver[0]
                self.assertTrue(verstring, msg="Could not find upstream version for %s" % k[0])
                r = bb.utils.vercmp_string(v, verstring)
                self.assertTrue(r == -1 or r == 0, msg="Package %s, version: %s <= %s" % (k[0], v, verstring))
        finally:
            server.stop()


class FetchCheckStatusTest(FetcherTest):
    test_wget_uris = ["http://downloads.yoctoproject.org/releases/sato/sato-engine-0.1.tar.gz",
                      "http://downloads.yoctoproject.org/releases/sato/sato-engine-0.2.tar.gz",
                      "http://downloads.yoctoproject.org/releases/sato/sato-engine-0.3.tar.gz",
                      "https://yoctoproject.org/",
                      "https://yoctoproject.org/documentation",
                      "http://downloads.yoctoproject.org/releases/opkg/opkg-0.1.7.tar.gz",
                      "http://downloads.yoctoproject.org/releases/opkg/opkg-0.3.0.tar.gz",
                      "ftp://sourceware.org/pub/libffi/libffi-1.20.tar.gz",
                      # GitHub releases are hosted on Amazon S3, which doesn't support HEAD
                      "https://github.com/kergoth/tslib/releases/download/1.1/tslib-1.1.tar.xz"
                      ]

    @skipIfNoNetwork()
    def test_wget_checkstatus(self):
        fetch = bb.fetch2.Fetch(self.test_wget_uris, self.d)
        for u in self.test_wget_uris:
            with self.subTest(url=u):
                ud = fetch.ud[u]
                m = ud.method
                ret = m.checkstatus(fetch, ud, self.d)
                self.assertTrue(ret, msg="URI %s, can't check status" % (u))

    @skipIfNoNetwork()
    def test_wget_checkstatus_connection_cache(self):
        from bb.fetch2 import FetchConnectionCache

        connection_cache = FetchConnectionCache()
        fetch = bb.fetch2.Fetch(self.test_wget_uris, self.d,
                    connection_cache = connection_cache)

        for u in self.test_wget_uris:
            with self.subTest(url=u):
                ud = fetch.ud[u]
                m = ud.method
                ret = m.checkstatus(fetch, ud, self.d)
                self.assertTrue(ret, msg="URI %s, can't check status" % (u))

        connection_cache.close_connections()


class GitMakeShallowTest(FetcherTest):
    def setUp(self):
        FetcherTest.setUp(self)
        self.gitdir = os.path.join(self.tempdir, 'gitshallow')
        bb.utils.mkdirhier(self.gitdir)
        bb.process.run('git init', cwd=self.gitdir)
        bb.process.run('git config user.email "you@example.com"', cwd=self.gitdir)
        bb.process.run('git config user.name "Your Name"', cwd=self.gitdir)

    def assertRefs(self, expected_refs):
        actual_refs = self.git(['for-each-ref', '--format=%(refname)']).splitlines()
        full_expected = self.git(['rev-parse', '--symbolic-full-name'] + expected_refs).splitlines()
        self.assertEqual(sorted(full_expected), sorted(actual_refs))

    def assertRevCount(self, expected_count, args=None):
        if args is None:
            args = ['HEAD']
        revs = self.git(['rev-list'] + args)
        actual_count = len(revs.splitlines())
        self.assertEqual(expected_count, actual_count, msg='Object count `%d` is not the expected `%d`' % (actual_count, expected_count))

    def git(self, cmd):
        if isinstance(cmd, str):
            cmd = 'git ' + cmd
        else:
            cmd = ['git'] + cmd
        return bb.process.run(cmd, cwd=self.gitdir)[0]

    def make_shallow(self, args=None):
        if args is None:
            args = ['HEAD']
        return bb.process.run([bb.fetch2.git.Git.make_shallow_path] + args, cwd=self.gitdir)

    def add_empty_file(self, path, msg=None):
        if msg is None:
            msg = path
        open(os.path.join(self.gitdir, path), 'w').close()
        self.git(['add', path])
        self.git(['commit', '-m', msg, path])

    def test_make_shallow_single_branch_no_merge(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.assertRevCount(2)
        self.make_shallow()
        self.assertRevCount(1)

    def test_make_shallow_single_branch_one_merge(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.git('checkout -b a_branch')
        self.add_empty_file('c')
        self.git('checkout master')
        self.add_empty_file('d')
        self.git('merge --no-ff --no-edit a_branch')
        self.git('branch -d a_branch')
        self.add_empty_file('e')
        self.assertRevCount(6)
        self.make_shallow(['HEAD~2'])
        self.assertRevCount(5)

    def test_make_shallow_at_merge(self):
        self.add_empty_file('a')
        self.git('checkout -b a_branch')
        self.add_empty_file('b')
        self.git('checkout master')
        self.git('merge --no-ff --no-edit a_branch')
        self.git('branch -d a_branch')
        self.assertRevCount(3)
        self.make_shallow()
        self.assertRevCount(1)

    def test_make_shallow_annotated_tag(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.git('tag -a -m a_tag a_tag')
        self.assertRevCount(2)
        self.make_shallow(['a_tag'])
        self.assertRevCount(1)

    def test_make_shallow_multi_ref(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.git('checkout -b a_branch')
        self.add_empty_file('c')
        self.git('checkout master')
        self.add_empty_file('d')
        self.git('checkout -b a_branch_2')
        self.add_empty_file('a_tag')
        self.git('tag a_tag')
        self.git('checkout master')
        self.git('branch -D a_branch_2')
        self.add_empty_file('e')
        self.assertRevCount(6, ['--all'])
        self.make_shallow()
        self.assertRevCount(5, ['--all'])

    def test_make_shallow_multi_ref_trim(self):
        self.add_empty_file('a')
        self.git('checkout -b a_branch')
        self.add_empty_file('c')
        self.git('checkout master')
        self.assertRevCount(1)
        self.assertRevCount(2, ['--all'])
        self.assertRefs(['master', 'a_branch'])
        self.make_shallow(['-r', 'master', 'HEAD'])
        self.assertRevCount(1, ['--all'])
        self.assertRefs(['master'])

    def test_make_shallow_noop(self):
        self.add_empty_file('a')
        self.assertRevCount(1)
        self.make_shallow()
        self.assertRevCount(1)

    @skipIfNoNetwork()
    def test_make_shallow_bitbake(self):
        self.git('remote add origin https://github.com/openembedded/bitbake')
        self.git('fetch --tags origin')
        orig_revs = len(self.git('rev-list --all').splitlines())
        self.make_shallow(['refs/tags/1.10.0'])
        self.assertRevCount(orig_revs - 1746, ['--all'])

class GitShallowTest(FetcherTest):
    def setUp(self):
        FetcherTest.setUp(self)
        self.gitdir = os.path.join(self.tempdir, 'git')
        self.srcdir = os.path.join(self.tempdir, 'gitsource')

        bb.utils.mkdirhier(self.srcdir)
        self.git('init', cwd=self.srcdir)
        self.git('config user.email "you@example.com"', cwd=self.srcdir)
        self.git('config user.name "Your Name"', cwd=self.srcdir)
        self.d.setVar('WORKDIR', self.tempdir)
        self.d.setVar('S', self.gitdir)
        self.d.delVar('PREMIRRORS')
        self.d.delVar('MIRRORS')

        uri = 'git://%s;protocol=file;subdir=${S}' % self.srcdir
        self.d.setVar('SRC_URI', uri)
        self.d.setVar('SRCREV', '${AUTOREV}')
        self.d.setVar('AUTOREV', '${@bb.fetch2.get_autorev(d)}')

        self.d.setVar('BB_GIT_SHALLOW', '1')
        self.d.setVar('BB_GENERATE_MIRROR_TARBALLS', '0')
        self.d.setVar('BB_GENERATE_SHALLOW_TARBALLS', '1')

    def assertRefs(self, expected_refs, cwd=None):
        if cwd is None:
            cwd = self.gitdir
        actual_refs = self.git(['for-each-ref', '--format=%(refname)'], cwd=cwd).splitlines()
        full_expected = self.git(['rev-parse', '--symbolic-full-name'] + expected_refs, cwd=cwd).splitlines()
        self.assertEqual(sorted(set(full_expected)), sorted(set(actual_refs)))

    def assertRevCount(self, expected_count, args=None, cwd=None):
        if args is None:
            args = ['HEAD']
        if cwd is None:
            cwd = self.gitdir
        revs = self.git(['rev-list'] + args, cwd=cwd)
        actual_count = len(revs.splitlines())
        self.assertEqual(expected_count, actual_count, msg='Object count `%d` is not the expected `%d`' % (actual_count, expected_count))

    def git(self, cmd, cwd=None):
        if isinstance(cmd, str):
            cmd = 'git ' + cmd
        else:
            cmd = ['git'] + cmd
        if cwd is None:
            cwd = self.gitdir
        return bb.process.run(cmd, cwd=cwd)[0]

    def add_empty_file(self, path, cwd=None, msg=None):
        if msg is None:
            msg = path
        if cwd is None:
            cwd = self.srcdir
        open(os.path.join(cwd, path), 'w').close()
        self.git(['add', path], cwd)
        self.git(['commit', '-m', msg, path], cwd)

    def fetch(self, uri=None):
        if uri is None:
            uris = self.d.getVar('SRC_URI').split()
            uri = uris[0]
            d = self.d
        else:
            d = self.d.createCopy()
            d.setVar('SRC_URI', uri)
            uri = d.expand(uri)
            uris = [uri]

        fetcher = bb.fetch2.Fetch(uris, d)
        fetcher.download()
        ud = fetcher.ud[uri]
        return fetcher, ud

    def fetch_and_unpack(self, uri=None):
        fetcher, ud = self.fetch(uri)
        fetcher.unpack(self.d.getVar('WORKDIR'))
        assert os.path.exists(self.d.getVar('S'))
        return fetcher, ud

    def fetch_shallow(self, uri=None, disabled=False, keepclone=False):
        """Fetch a uri, generating a shallow tarball, then unpack using it"""
        fetcher, ud = self.fetch_and_unpack(uri)
        assert os.path.exists(ud.clonedir), 'Git clone in DLDIR (%s) does not exist for uri %s' % (ud.clonedir, uri)

        # Confirm that the unpacked repo is unshallow
        if not disabled:
            assert os.path.exists(os.path.join(self.dldir, ud.mirrortarballs[0]))

        # fetch and unpack, from the shallow tarball
        bb.utils.remove(self.gitdir, recurse=True)
        bb.process.run('chmod u+w -R "%s"' % ud.clonedir)
        bb.utils.remove(ud.clonedir, recurse=True)
        bb.utils.remove(ud.clonedir.replace('gitsource', 'gitsubmodule'), recurse=True)

        # confirm that the unpacked repo is used when no git clone or git
        # mirror tarball is available
        fetcher, ud = self.fetch_and_unpack(uri)
        if not disabled:
            assert os.path.exists(os.path.join(self.gitdir, '.git', 'shallow')), 'Unpacked git repository at %s is not shallow' % self.gitdir
        else:
            assert not os.path.exists(os.path.join(self.gitdir, '.git', 'shallow')), 'Unpacked git repository at %s is shallow' % self.gitdir
        return fetcher, ud

    def test_shallow_disabled(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.assertRevCount(2, cwd=self.srcdir)

        self.d.setVar('BB_GIT_SHALLOW', '0')
        self.fetch_shallow(disabled=True)
        self.assertRevCount(2)

    def test_shallow_nobranch(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.assertRevCount(2, cwd=self.srcdir)

        srcrev = self.git('rev-parse HEAD', cwd=self.srcdir).strip()
        self.d.setVar('SRCREV', srcrev)
        uri = self.d.getVar('SRC_URI').split()[0]
        uri = '%s;nobranch=1;bare=1' % uri

        self.fetch_shallow(uri)
        self.assertRevCount(1)

        # shallow refs are used to ensure the srcrev sticks around when we
        # have no other branches referencing it
        self.assertRefs(['refs/shallow/default'])

    def test_shallow_default_depth_1(self):
        # Create initial git repo
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.assertRevCount(2, cwd=self.srcdir)

        self.fetch_shallow()
        self.assertRevCount(1)

    def test_shallow_depth_0_disables(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.assertRevCount(2, cwd=self.srcdir)

        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '0')
        self.fetch_shallow(disabled=True)
        self.assertRevCount(2)

    def test_shallow_depth_default_override(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.assertRevCount(2, cwd=self.srcdir)

        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '2')
        self.d.setVar('BB_GIT_SHALLOW_DEPTH_default', '1')
        self.fetch_shallow()
        self.assertRevCount(1)

    def test_shallow_depth_default_override_disable(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.add_empty_file('c')
        self.assertRevCount(3, cwd=self.srcdir)

        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '0')
        self.d.setVar('BB_GIT_SHALLOW_DEPTH_default', '2')
        self.fetch_shallow()
        self.assertRevCount(2)

    def test_current_shallow_out_of_date_clone(self):
        # Create initial git repo
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.add_empty_file('c')
        self.assertRevCount(3, cwd=self.srcdir)

        # Clone and generate mirror tarball
        fetcher, ud = self.fetch()

        # Ensure we have a current mirror tarball, but an out of date clone
        self.git('update-ref refs/heads/master refs/heads/master~1', cwd=ud.clonedir)
        self.assertRevCount(2, cwd=ud.clonedir)

        # Fetch and unpack, from the current tarball, not the out of date clone
        bb.utils.remove(self.gitdir, recurse=True)
        fetcher, ud = self.fetch()
        fetcher.unpack(self.d.getVar('WORKDIR'))
        self.assertRevCount(1)

    def test_shallow_single_branch_no_merge(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.assertRevCount(2, cwd=self.srcdir)

        self.fetch_shallow()
        self.assertRevCount(1)
        assert os.path.exists(os.path.join(self.gitdir, 'a'))
        assert os.path.exists(os.path.join(self.gitdir, 'b'))

    def test_shallow_no_dangling(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.assertRevCount(2, cwd=self.srcdir)

        self.fetch_shallow()
        self.assertRevCount(1)
        assert not self.git('fsck --dangling')

    def test_shallow_srcrev_branch_truncation(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        b_commit = self.git('rev-parse HEAD', cwd=self.srcdir).rstrip()
        self.add_empty_file('c')
        self.assertRevCount(3, cwd=self.srcdir)

        self.d.setVar('SRCREV', b_commit)
        self.fetch_shallow()

        # The 'c' commit was removed entirely, and 'a' was removed from history
        self.assertRevCount(1, ['--all'])
        self.assertEqual(self.git('rev-parse HEAD').strip(), b_commit)
        assert os.path.exists(os.path.join(self.gitdir, 'a'))
        assert os.path.exists(os.path.join(self.gitdir, 'b'))
        assert not os.path.exists(os.path.join(self.gitdir, 'c'))

    def test_shallow_ref_pruning(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.git('branch a_branch', cwd=self.srcdir)
        self.assertRefs(['master', 'a_branch'], cwd=self.srcdir)
        self.assertRevCount(2, cwd=self.srcdir)

        self.fetch_shallow()

        self.assertRefs(['master', 'origin/master'])
        self.assertRevCount(1)

    def test_shallow_submodules(self):
        self.add_empty_file('a')
        self.add_empty_file('b')

        smdir = os.path.join(self.tempdir, 'gitsubmodule')
        bb.utils.mkdirhier(smdir)
        self.git('init', cwd=smdir)
        self.git('config user.email "you@example.com"', cwd=smdir)
        self.git('config user.name "Your Name"', cwd=smdir)
        # Make this look like it was cloned from a remote...
        self.git('config --add remote.origin.url "%s"' % smdir, cwd=smdir)
        self.git('config --add remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"', cwd=smdir)
        self.add_empty_file('asub', cwd=smdir)
        self.add_empty_file('bsub', cwd=smdir)

        self.git('submodule init', cwd=self.srcdir)
        self.git('submodule add file://%s' % smdir, cwd=self.srcdir)
        self.git('submodule update', cwd=self.srcdir)
        self.git('commit -m submodule -a', cwd=self.srcdir)

        uri = 'gitsm://%s;protocol=file;subdir=${S}' % self.srcdir
        fetcher, ud = self.fetch_shallow(uri)

        # Verify the main repository is shallow
        self.assertRevCount(1)

        # Verify the gitsubmodule directory is present
        assert os.listdir(os.path.join(self.gitdir, 'gitsubmodule'))

        # Verify the submodule is also shallow
        self.assertRevCount(1, cwd=os.path.join(self.gitdir, 'gitsubmodule'))

    def test_shallow_submodule_mirrors(self):
        self.add_empty_file('a')
        self.add_empty_file('b')

        smdir = os.path.join(self.tempdir, 'gitsubmodule')
        bb.utils.mkdirhier(smdir)
        self.git('init', cwd=smdir)
        self.git('config user.email "you@example.com"', cwd=smdir)
        self.git('config user.name "Your Name"', cwd=smdir)
        # Make this look like it was cloned from a remote...
        self.git('config --add remote.origin.url "%s"' % smdir, cwd=smdir)
        self.git('config --add remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"', cwd=smdir)
        self.add_empty_file('asub', cwd=smdir)
        self.add_empty_file('bsub', cwd=smdir)

        self.git('submodule init', cwd=self.srcdir)
        self.git('submodule add file://%s' % smdir, cwd=self.srcdir)
        self.git('submodule update', cwd=self.srcdir)
        self.git('commit -m submodule -a', cwd=self.srcdir)

        uri = 'gitsm://%s;protocol=file;subdir=${S}' % self.srcdir

        # Fetch once to generate the shallow tarball
        fetcher, ud = self.fetch(uri)

        # Set up the mirror
        mirrordir = os.path.join(self.tempdir, 'mirror')
        os.rename(self.dldir, mirrordir)
        self.d.setVar('PREMIRRORS', 'gitsm://.*/.* file://%s/\n' % mirrordir)

        # Fetch from the mirror
        bb.utils.remove(self.dldir, recurse=True)
        bb.utils.remove(self.gitdir, recurse=True)
        self.fetch_and_unpack(uri)

        # Verify the main repository is shallow
        self.assertRevCount(1)

        # Verify the gitsubmodule directory is present
        assert os.listdir(os.path.join(self.gitdir, 'gitsubmodule'))

        # Verify the submodule is also shallow
        self.assertRevCount(1, cwd=os.path.join(self.gitdir, 'gitsubmodule'))

    if any(os.path.exists(os.path.join(p, 'git-annex')) for p in os.environ.get('PATH').split(':')):
        def test_shallow_annex(self):
            self.add_empty_file('a')
            self.add_empty_file('b')
            self.git('annex init', cwd=self.srcdir)
            open(os.path.join(self.srcdir, 'c'), 'w').close()
            self.git('annex add c', cwd=self.srcdir)
            self.git('commit --author "Foo Bar <foo@bar>" -m annex-c -a', cwd=self.srcdir)
            bb.process.run('chmod u+w -R %s' % self.srcdir)

            uri = 'gitannex://%s;protocol=file;subdir=${S}' % self.srcdir
            fetcher, ud = self.fetch_shallow(uri)

            self.assertRevCount(1)
            assert './.git/annex/' in bb.process.run('tar -tzf %s' % os.path.join(self.dldir, ud.mirrortarballs[0]))[0]
            assert os.path.exists(os.path.join(self.gitdir, 'c'))

    def test_shallow_multi_one_uri(self):
        # Create initial git repo
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.git('checkout -b a_branch', cwd=self.srcdir)
        self.add_empty_file('c')
        self.add_empty_file('d')
        self.git('checkout master', cwd=self.srcdir)
        self.git('tag v0.0 a_branch', cwd=self.srcdir)
        self.add_empty_file('e')
        self.git('merge --no-ff --no-edit a_branch', cwd=self.srcdir)
        self.add_empty_file('f')
        self.assertRevCount(7, cwd=self.srcdir)

        uri = self.d.getVar('SRC_URI').split()[0]
        uri = '%s;branch=master,a_branch;name=master,a_branch' % uri

        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '0')
        self.d.setVar('BB_GIT_SHALLOW_REVS', 'v0.0')
        self.d.setVar('SRCREV_master', '${AUTOREV}')
        self.d.setVar('SRCREV_a_branch', '${AUTOREV}')

        self.fetch_shallow(uri)

        self.assertRevCount(5)
        self.assertRefs(['master', 'origin/master', 'origin/a_branch'])

    def test_shallow_multi_one_uri_depths(self):
        # Create initial git repo
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.git('checkout -b a_branch', cwd=self.srcdir)
        self.add_empty_file('c')
        self.add_empty_file('d')
        self.git('checkout master', cwd=self.srcdir)
        self.add_empty_file('e')
        self.git('merge --no-ff --no-edit a_branch', cwd=self.srcdir)
        self.add_empty_file('f')
        self.assertRevCount(7, cwd=self.srcdir)

        uri = self.d.getVar('SRC_URI').split()[0]
        uri = '%s;branch=master,a_branch;name=master,a_branch' % uri

        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '0')
        self.d.setVar('BB_GIT_SHALLOW_DEPTH_master', '3')
        self.d.setVar('BB_GIT_SHALLOW_DEPTH_a_branch', '1')
        self.d.setVar('SRCREV_master', '${AUTOREV}')
        self.d.setVar('SRCREV_a_branch', '${AUTOREV}')

        self.fetch_shallow(uri)

        self.assertRevCount(4, ['--all'])
        self.assertRefs(['master', 'origin/master', 'origin/a_branch'])

    def test_shallow_clone_preferred_over_shallow(self):
        self.add_empty_file('a')
        self.add_empty_file('b')

        # Fetch once to generate the shallow tarball
        fetcher, ud = self.fetch()
        assert os.path.exists(os.path.join(self.dldir, ud.mirrortarballs[0]))

        # Fetch and unpack with both the clonedir and shallow tarball available
        bb.utils.remove(self.gitdir, recurse=True)
        fetcher, ud = self.fetch_and_unpack()

        # The unpacked tree should *not* be shallow
        self.assertRevCount(2)
        assert not os.path.exists(os.path.join(self.gitdir, '.git', 'shallow'))

    def test_shallow_mirrors(self):
        self.add_empty_file('a')
        self.add_empty_file('b')

        # Fetch once to generate the shallow tarball
        fetcher, ud = self.fetch()
        mirrortarball = ud.mirrortarballs[0]
        assert os.path.exists(os.path.join(self.dldir, mirrortarball))

        # Set up the mirror
        mirrordir = os.path.join(self.tempdir, 'mirror')
        bb.utils.mkdirhier(mirrordir)
        self.d.setVar('PREMIRRORS', 'git://.*/.* file://%s/\n' % mirrordir)

        os.rename(os.path.join(self.dldir, mirrortarball),
                  os.path.join(mirrordir, mirrortarball))

        # Fetch from the mirror
        bb.utils.remove(self.dldir, recurse=True)
        bb.utils.remove(self.gitdir, recurse=True)
        self.fetch_and_unpack()
        self.assertRevCount(1)

    def test_shallow_invalid_depth(self):
        self.add_empty_file('a')
        self.add_empty_file('b')

        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '-12')
        with self.assertRaises(bb.fetch2.FetchError):
            self.fetch()

    def test_shallow_invalid_depth_default(self):
        self.add_empty_file('a')
        self.add_empty_file('b')

        self.d.setVar('BB_GIT_SHALLOW_DEPTH_default', '-12')
        with self.assertRaises(bb.fetch2.FetchError):
            self.fetch()

    def test_shallow_extra_refs(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.git('branch a_branch', cwd=self.srcdir)
        self.assertRefs(['master', 'a_branch'], cwd=self.srcdir)
        self.assertRevCount(2, cwd=self.srcdir)

        self.d.setVar('BB_GIT_SHALLOW_EXTRA_REFS', 'refs/heads/a_branch')
        self.fetch_shallow()

        self.assertRefs(['master', 'origin/master', 'origin/a_branch'])
        self.assertRevCount(1)

    def test_shallow_extra_refs_wildcard(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.git('branch a_branch', cwd=self.srcdir)
        self.git('tag v1.0', cwd=self.srcdir)
        self.assertRefs(['master', 'a_branch', 'v1.0'], cwd=self.srcdir)
        self.assertRevCount(2, cwd=self.srcdir)

        self.d.setVar('BB_GIT_SHALLOW_EXTRA_REFS', 'refs/tags/*')
        self.fetch_shallow()

        self.assertRefs(['master', 'origin/master', 'v1.0'])
        self.assertRevCount(1)

    def test_shallow_missing_extra_refs(self):
        self.add_empty_file('a')
        self.add_empty_file('b')

        self.d.setVar('BB_GIT_SHALLOW_EXTRA_REFS', 'refs/heads/foo')
        with self.assertRaises(bb.fetch2.FetchError):
            self.fetch()

    def test_shallow_missing_extra_refs_wildcard(self):
        self.add_empty_file('a')
        self.add_empty_file('b')

        self.d.setVar('BB_GIT_SHALLOW_EXTRA_REFS', 'refs/tags/*')
        self.fetch()

    def test_shallow_remove_revs(self):
        # Create initial git repo
        self.add_empty_file('a')
        self.add_empty_file('b')
        self.git('checkout -b a_branch', cwd=self.srcdir)
        self.add_empty_file('c')
        self.add_empty_file('d')
        self.git('checkout master', cwd=self.srcdir)
        self.git('tag v0.0 a_branch', cwd=self.srcdir)
        self.add_empty_file('e')
        self.git('merge --no-ff --no-edit a_branch', cwd=self.srcdir)
        self.git('branch -d a_branch', cwd=self.srcdir)
        self.add_empty_file('f')
        self.assertRevCount(7, cwd=self.srcdir)

        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '0')
        self.d.setVar('BB_GIT_SHALLOW_REVS', 'v0.0')

        self.fetch_shallow()

        self.assertRevCount(5)

    def test_shallow_invalid_revs(self):
        self.add_empty_file('a')
        self.add_empty_file('b')

        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '0')
        self.d.setVar('BB_GIT_SHALLOW_REVS', 'v0.0')

        with self.assertRaises(bb.fetch2.FetchError):
            self.fetch()

    def test_shallow_fetch_missing_revs(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        fetcher, ud = self.fetch(self.d.getVar('SRC_URI'))
        self.git('tag v0.0 master', cwd=self.srcdir)
        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '0')
        self.d.setVar('BB_GIT_SHALLOW_REVS', 'v0.0')
        self.fetch_shallow()

    def test_shallow_fetch_missing_revs_fails(self):
        self.add_empty_file('a')
        self.add_empty_file('b')
        fetcher, ud = self.fetch(self.d.getVar('SRC_URI'))
        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '0')
        self.d.setVar('BB_GIT_SHALLOW_REVS', 'v0.0')

        with self.assertRaises(bb.fetch2.FetchError), self.assertLogs("BitBake.Fetcher", level="ERROR") as cm:
            self.fetch_shallow()
        self.assertIn("Unable to find revision v0.0 even from upstream", cm.output[0])

    @skipIfNoNetwork()
    def test_bitbake(self):
        self.git('remote add --mirror=fetch origin https://github.com/openembedded/bitbake', cwd=self.srcdir)
        self.git('config core.bare true', cwd=self.srcdir)
        self.git('fetch', cwd=self.srcdir)

        self.d.setVar('BB_GIT_SHALLOW_DEPTH', '0')
        # Note that the 1.10.0 tag is annotated, so this also tests
        # reference of an annotated vs unannotated tag
        self.d.setVar('BB_GIT_SHALLOW_REVS', '1.10.0')

        self.fetch_shallow()

        # Confirm that the history of 1.10.0 was removed
        orig_revs = len(self.git('rev-list master', cwd=self.srcdir).splitlines())
        revs = len(self.git('rev-list master').splitlines())
        self.assertNotEqual(orig_revs, revs)
        self.assertRefs(['master', 'origin/master'])
        self.assertRevCount(orig_revs - 1758)

    def test_that_unpack_throws_an_error_when_the_git_clone_nor_shallow_tarball_exist(self):
        self.add_empty_file('a')
        fetcher, ud = self.fetch()
        bb.utils.remove(self.gitdir, recurse=True)
        bb.utils.remove(self.dldir, recurse=True)

        with self.assertRaises(bb.fetch2.UnpackError) as context:
            fetcher.unpack(self.d.getVar('WORKDIR'))

        self.assertIn("No up to date source found", context.exception.msg)
        self.assertIn("clone directory not available or not up to date", context.exception.msg)

    @skipIfNoNetwork()
    def test_that_unpack_does_work_when_using_git_shallow_tarball_but_tarball_is_not_available(self):
        self.d.setVar('SRCREV', 'e5939ff608b95cdd4d0ab0e1935781ab9a276ac0')
        self.d.setVar('BB_GIT_SHALLOW', '1')
        self.d.setVar('BB_GENERATE_SHALLOW_TARBALLS', '1')
        fetcher = bb.fetch.Fetch(["git://git.yoctoproject.org/fstests"], self.d)
        fetcher.download()

        bb.utils.remove(self.dldir + "/*.tar.gz")
        fetcher.unpack(self.unpackdir)

        dir = os.listdir(self.unpackdir + "/git/")
        self.assertIn("fstests.doap", dir)

class GitLfsTest(FetcherTest):
    def setUp(self):
        FetcherTest.setUp(self)

        self.gitdir = os.path.join(self.tempdir, 'git')
        self.srcdir = os.path.join(self.tempdir, 'gitsource')
        
        self.d.setVar('WORKDIR', self.tempdir)
        self.d.setVar('S', self.gitdir)
        self.d.delVar('PREMIRRORS')
        self.d.delVar('MIRRORS')

        self.d.setVar('SRCREV', '${AUTOREV}')
        self.d.setVar('AUTOREV', '${@bb.fetch2.get_autorev(d)}')

        bb.utils.mkdirhier(self.srcdir)
        self.git('init', cwd=self.srcdir)
        self.git('config user.email "you@example.com"', cwd=self.srcdir)
        self.git('config user.name "Your Name"', cwd=self.srcdir)
        with open(os.path.join(self.srcdir, '.gitattributes'), 'wt') as attrs:
            attrs.write('*.mp3 filter=lfs -text')
        self.git(['add', '.gitattributes'], cwd=self.srcdir)
        self.git(['commit', '-m', "attributes", '.gitattributes'], cwd=self.srcdir)

    def git(self, cmd, cwd=None):
        if isinstance(cmd, str):
            cmd = 'git ' + cmd
        else:
            cmd = ['git'] + cmd
        if cwd is None:
            cwd = self.gitdir
        return bb.process.run(cmd, cwd=cwd)[0]

    def fetch(self, uri=None, download=True):
        uris = self.d.getVar('SRC_URI').split()
        uri = uris[0]
        d = self.d

        fetcher = bb.fetch2.Fetch(uris, d)
        if download:
            fetcher.download()
        ud = fetcher.ud[uri]
        return fetcher, ud

    def test_lfs_enabled(self):
        import shutil

        uri = 'git://%s;protocol=file;subdir=${S};lfs=1' % self.srcdir
        self.d.setVar('SRC_URI', uri)

        # Careful: suppress initial attempt at downloading until
        # we know whether git-lfs is installed.
        fetcher, ud = self.fetch(uri=None, download=False)
        self.assertIsNotNone(ud.method._find_git_lfs)

        # If git-lfs can be found, the unpack should be successful. Only
        # attempt this with the real live copy of git-lfs installed.
        if ud.method._find_git_lfs(self.d):
            fetcher.download()
            shutil.rmtree(self.gitdir, ignore_errors=True)
            fetcher.unpack(self.d.getVar('WORKDIR'))

        # If git-lfs cannot be found, the unpack should throw an error
        with self.assertRaises(bb.fetch2.FetchError):
            fetcher.download()
            ud.method._find_git_lfs = lambda d: False
            shutil.rmtree(self.gitdir, ignore_errors=True)
            fetcher.unpack(self.d.getVar('WORKDIR'))

    def test_lfs_disabled(self):
        import shutil

        uri = 'git://%s;protocol=file;subdir=${S};lfs=0' % self.srcdir
        self.d.setVar('SRC_URI', uri)

        # In contrast to test_lfs_enabled(), allow the implicit download
        # done by self.fetch() to occur here. The point of this test case
        # is to verify that the fetcher can survive even if the source
        # repository has Git LFS usage configured.
        fetcher, ud = self.fetch()
        self.assertIsNotNone(ud.method._find_git_lfs)

        # If git-lfs can be found, the unpack should be successful. A
        # live copy of git-lfs is not required for this case, so
        # unconditionally forge its presence.
        ud.method._find_git_lfs = lambda d: True
        shutil.rmtree(self.gitdir, ignore_errors=True)
        fetcher.unpack(self.d.getVar('WORKDIR'))

        # If git-lfs cannot be found, the unpack should be successful
        ud.method._find_git_lfs = lambda d: False
        shutil.rmtree(self.gitdir, ignore_errors=True)
        fetcher.unpack(self.d.getVar('WORKDIR'))

class GitURLWithSpacesTest(FetcherTest):
    test_git_urls = {
        "git://tfs-example.org:22/tfs/example%20path/example.git" : {
            'url': 'git://tfs-example.org:22/tfs/example%20path/example.git',
            'gitsrcname': 'tfs-example.org.22.tfs.example_path.example.git',
            'path': '/tfs/example path/example.git'
        },
        "git://tfs-example.org:22/tfs/example%20path/example%20repo.git" : {
            'url': 'git://tfs-example.org:22/tfs/example%20path/example%20repo.git',
            'gitsrcname': 'tfs-example.org.22.tfs.example_path.example_repo.git',
            'path': '/tfs/example path/example repo.git'
        }
    }

    def test_urls(self):

        # Set fake SRCREV to stop git fetcher from trying to contact non-existent git repo
        self.d.setVar('SRCREV', '82ea737a0b42a8b53e11c9cde141e9e9c0bd8c40')

        for test_git_url, ref in self.test_git_urls.items():

            fetcher = bb.fetch.Fetch([test_git_url], self.d)
            ud = fetcher.ud[fetcher.urls[0]]

            self.assertEqual(ud.url, ref['url'])
            self.assertEqual(ud.path, ref['path'])
            self.assertEqual(ud.localfile, os.path.join(self.dldir, "git2", ref['gitsrcname']))
            self.assertEqual(ud.localpath, os.path.join(self.dldir, "git2", ref['gitsrcname']))
            self.assertEqual(ud.lockfile, os.path.join(self.dldir, "git2", ref['gitsrcname'] + '.lock'))
            self.assertEqual(ud.clonedir, os.path.join(self.dldir, "git2", ref['gitsrcname']))
            self.assertEqual(ud.fullmirror, os.path.join(self.dldir, "git2_" + ref['gitsrcname'] + '.tar.gz'))

class NPMTest(FetcherTest):
    def skipIfNoNpm():
        import shutil
        if not shutil.which('npm'):
            return unittest.skip('npm not installed, tests being skipped')
        return lambda f: f

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm(self):
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=1.0.0'
        fetcher = bb.fetch.Fetch([url], self.d)
        ud = fetcher.ud[fetcher.urls[0]]
        fetcher.download()
        self.assertTrue(os.path.exists(ud.localpath))
        self.assertTrue(os.path.exists(ud.localpath + '.done'))
        self.assertTrue(os.path.exists(ud.resolvefile))
        fetcher.unpack(self.unpackdir)
        unpackdir = os.path.join(self.unpackdir, 'npm')
        self.assertTrue(os.path.exists(os.path.join(unpackdir, 'package.json')))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_bad_checksum(self):
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=1.0.0'
        # Fetch once to get a tarball
        fetcher = bb.fetch.Fetch([url], self.d)
        ud = fetcher.ud[fetcher.urls[0]]
        fetcher.download()
        self.assertTrue(os.path.exists(ud.localpath))
        # Modify the tarball
        bad = b'bad checksum'
        with open(ud.localpath, 'wb') as f:
            f.write(bad)
        # Verify that the tarball is fetched again
        fetcher.download()
        badsum = hashlib.sha512(bad).hexdigest()
        self.assertTrue(os.path.exists(ud.localpath + '_bad-checksum_' + badsum))
        self.assertTrue(os.path.exists(ud.localpath))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_premirrors(self):
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=1.0.0'
        # Fetch once to get a tarball
        fetcher = bb.fetch.Fetch([url], self.d)
        ud = fetcher.ud[fetcher.urls[0]]
        fetcher.download()
        self.assertTrue(os.path.exists(ud.localpath))
        # Setup the mirror
        mirrordir = os.path.join(self.tempdir, 'mirror')
        bb.utils.mkdirhier(mirrordir)
        os.replace(ud.localpath, os.path.join(mirrordir, os.path.basename(ud.localpath)))
        self.d.setVar('PREMIRRORS', 'https?$://.*/.* file://%s/\n' % mirrordir)
        self.d.setVar('BB_FETCH_PREMIRRORONLY', '1')
        # Fetch again
        self.assertFalse(os.path.exists(ud.localpath))
        fetcher.download()
        self.assertTrue(os.path.exists(ud.localpath))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_mirrors(self):
        # Fetch once to get a tarball
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=1.0.0'
        fetcher = bb.fetch.Fetch([url], self.d)
        ud = fetcher.ud[fetcher.urls[0]]
        fetcher.download()
        self.assertTrue(os.path.exists(ud.localpath))
        # Setup the mirror
        mirrordir = os.path.join(self.tempdir, 'mirror')
        bb.utils.mkdirhier(mirrordir)
        os.replace(ud.localpath, os.path.join(mirrordir, os.path.basename(ud.localpath)))
        self.d.setVar('MIRRORS', 'https?$://.*/.* file://%s/\n' % mirrordir)
        # Update the resolved url to an invalid url
        with open(ud.resolvefile, 'r') as f:
            url = f.read()
        uri = URI(url)
        uri.path = '/invalid'
        with open(ud.resolvefile, 'w') as f:
            f.write(str(uri))
        # Fetch again
        self.assertFalse(os.path.exists(ud.localpath))
        fetcher.download()
        self.assertTrue(os.path.exists(ud.localpath))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_destsuffix_downloadfilename(self):
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=1.0.0;destsuffix=foo/bar;downloadfilename=foo-bar.tgz'
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        self.assertTrue(os.path.exists(os.path.join(self.dldir, 'foo-bar.tgz')))
        fetcher.unpack(self.unpackdir)
        unpackdir = os.path.join(self.unpackdir, 'foo', 'bar')
        self.assertTrue(os.path.exists(os.path.join(unpackdir, 'package.json')))

    def test_npm_no_network_no_tarball(self):
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=1.0.0'
        self.d.setVar('BB_NO_NETWORK', '1')
        fetcher = bb.fetch.Fetch([url], self.d)
        with self.assertRaises(bb.fetch2.NetworkAccess):
            fetcher.download()

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_no_network_with_tarball(self):
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=1.0.0'
        # Fetch once to get a tarball
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        # Disable network access
        self.d.setVar('BB_NO_NETWORK', '1')
        # Fetch again
        fetcher.download()
        fetcher.unpack(self.unpackdir)
        unpackdir = os.path.join(self.unpackdir, 'npm')
        self.assertTrue(os.path.exists(os.path.join(unpackdir, 'package.json')))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_registry_alternate(self):
        url = 'npm://registry.freajs.org;package=@savoirfairelinux/node-server-example;version=1.0.0'
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        fetcher.unpack(self.unpackdir)
        unpackdir = os.path.join(self.unpackdir, 'npm')
        self.assertTrue(os.path.exists(os.path.join(unpackdir, 'package.json')))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_version_latest(self):
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=latest'
        fetcher = bb.fetch.Fetch([url], self.d)
        fetcher.download()
        fetcher.unpack(self.unpackdir)
        unpackdir = os.path.join(self.unpackdir, 'npm')
        self.assertTrue(os.path.exists(os.path.join(unpackdir, 'package.json')))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_registry_invalid(self):
        url = 'npm://registry.invalid.org;package=@savoirfairelinux/node-server-example;version=1.0.0'
        fetcher = bb.fetch.Fetch([url], self.d)
        with self.assertRaises(bb.fetch2.FetchError):
            fetcher.download()

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_package_invalid(self):
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/invalid;version=1.0.0'
        fetcher = bb.fetch.Fetch([url], self.d)
        with self.assertRaises(bb.fetch2.FetchError):
            fetcher.download()

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_version_invalid(self):
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=invalid'
        with self.assertRaises(bb.fetch2.ParameterError):
            fetcher = bb.fetch.Fetch([url], self.d)

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_registry_none(self):
        url = 'npm://;package=@savoirfairelinux/node-server-example;version=1.0.0'
        with self.assertRaises(bb.fetch2.MalformedUrl):
            fetcher = bb.fetch.Fetch([url], self.d)

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_package_none(self):
        url = 'npm://registry.npmjs.org;version=1.0.0'
        with self.assertRaises(bb.fetch2.MissingParameterError):
            fetcher = bb.fetch.Fetch([url], self.d)

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npm_version_none(self):
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example'
        with self.assertRaises(bb.fetch2.MissingParameterError):
            fetcher = bb.fetch.Fetch([url], self.d)

    def create_shrinkwrap_file(self, data):
        import json
        datadir = os.path.join(self.tempdir, 'data')
        swfile = os.path.join(datadir, 'npm-shrinkwrap.json')
        bb.utils.mkdirhier(datadir)
        with open(swfile, 'w') as f:
            json.dump(data, f)
        # Also configure the S directory
        self.sdir = os.path.join(self.unpackdir, 'S')
        self.d.setVar('S', self.sdir)
        return swfile

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npmsw(self):
        swfile = self.create_shrinkwrap_file({
            'dependencies': {
                'array-flatten': {
                    'version': '1.1.1',
                    'resolved': 'https://registry.npmjs.org/array-flatten/-/array-flatten-1.1.1.tgz',
                    'integrity': 'sha1-ml9pkFGx5wczKPKgCJaLZOopVdI=',
                    'dependencies': {
                        'content-type': {
                            'version': 'https://registry.npmjs.org/content-type/-/content-type-1.0.4.tgz',
                            'integrity': 'sha512-hIP3EEPs8tB9AT1L+NUqtwOAps4mk2Zob89MWXMHjHWg9milF/j4osnnQLXBCBFBk/tvIG/tUc9mOUJiPBhPXA==',
                            'dependencies': {
                                'cookie': {
                                    'version': 'git+https://github.com/jshttp/cookie.git#aec1177c7da67e3b3273df96cf476824dbc9ae09',
                                    'from': 'git+https://github.com/jshttp/cookie.git'
                                }
                            }
                        }
                    }
                }
            }
        })
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile], self.d)
        fetcher.download()
        self.assertTrue(os.path.exists(os.path.join(self.dldir, 'npm2', 'array-flatten-1.1.1.tgz')))
        self.assertTrue(os.path.exists(os.path.join(self.dldir, 'npm2', 'content-type-1.0.4.tgz')))
        self.assertTrue(os.path.exists(os.path.join(self.dldir, 'git2', 'github.com.jshttp.cookie.git')))
        fetcher.unpack(self.unpackdir)
        self.assertTrue(os.path.exists(os.path.join(self.sdir, 'npm-shrinkwrap.json')))
        self.assertTrue(os.path.exists(os.path.join(self.sdir, 'node_modules', 'array-flatten', 'package.json')))
        self.assertTrue(os.path.exists(os.path.join(self.sdir, 'node_modules', 'array-flatten', 'node_modules', 'content-type', 'package.json')))
        self.assertTrue(os.path.exists(os.path.join(self.sdir, 'node_modules', 'array-flatten', 'node_modules', 'content-type', 'node_modules', 'cookie', 'package.json')))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npmsw_dev(self):
        swfile = self.create_shrinkwrap_file({
            'dependencies': {
                'array-flatten': {
                    'version': '1.1.1',
                    'resolved': 'https://registry.npmjs.org/array-flatten/-/array-flatten-1.1.1.tgz',
                    'integrity': 'sha1-ml9pkFGx5wczKPKgCJaLZOopVdI='
                },
                'content-type': {
                    'version': '1.0.4',
                    'resolved': 'https://registry.npmjs.org/content-type/-/content-type-1.0.4.tgz',
                    'integrity': 'sha512-hIP3EEPs8tB9AT1L+NUqtwOAps4mk2Zob89MWXMHjHWg9milF/j4osnnQLXBCBFBk/tvIG/tUc9mOUJiPBhPXA==',
                    'dev': True
                }
            }
        })
        # Fetch with dev disabled
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile], self.d)
        fetcher.download()
        self.assertTrue(os.path.exists(os.path.join(self.dldir, 'npm2', 'array-flatten-1.1.1.tgz')))
        self.assertFalse(os.path.exists(os.path.join(self.dldir, 'npm2', 'content-type-1.0.4.tgz')))
        # Fetch with dev enabled
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile + ';dev=1'], self.d)
        fetcher.download()
        self.assertTrue(os.path.exists(os.path.join(self.dldir, 'npm2', 'array-flatten-1.1.1.tgz')))
        self.assertTrue(os.path.exists(os.path.join(self.dldir, 'npm2', 'content-type-1.0.4.tgz')))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npmsw_destsuffix(self):
        swfile = self.create_shrinkwrap_file({
            'dependencies': {
                'array-flatten': {
                    'version': '1.1.1',
                    'resolved': 'https://registry.npmjs.org/array-flatten/-/array-flatten-1.1.1.tgz',
                    'integrity': 'sha1-ml9pkFGx5wczKPKgCJaLZOopVdI='
                }
            }
        })
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile + ';destsuffix=foo/bar'], self.d)
        fetcher.download()
        fetcher.unpack(self.unpackdir)
        self.assertTrue(os.path.exists(os.path.join(self.unpackdir, 'foo', 'bar', 'node_modules', 'array-flatten', 'package.json')))

    def test_npmsw_no_network_no_tarball(self):
        swfile = self.create_shrinkwrap_file({
            'dependencies': {
                'array-flatten': {
                    'version': '1.1.1',
                    'resolved': 'https://registry.npmjs.org/array-flatten/-/array-flatten-1.1.1.tgz',
                    'integrity': 'sha1-ml9pkFGx5wczKPKgCJaLZOopVdI='
                }
            }
        })
        self.d.setVar('BB_NO_NETWORK', '1')
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile], self.d)
        with self.assertRaises(bb.fetch2.NetworkAccess):
            fetcher.download()

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npmsw_no_network_with_tarball(self):
        # Fetch once to get a tarball
        fetcher = bb.fetch.Fetch(['npm://registry.npmjs.org;package=array-flatten;version=1.1.1'], self.d)
        fetcher.download()
        # Disable network access
        self.d.setVar('BB_NO_NETWORK', '1')
        # Fetch again
        swfile = self.create_shrinkwrap_file({
            'dependencies': {
                'array-flatten': {
                    'version': '1.1.1',
                    'resolved': 'https://registry.npmjs.org/array-flatten/-/array-flatten-1.1.1.tgz',
                    'integrity': 'sha1-ml9pkFGx5wczKPKgCJaLZOopVdI='
                }
            }
        })
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile], self.d)
        fetcher.download()
        fetcher.unpack(self.unpackdir)
        self.assertTrue(os.path.exists(os.path.join(self.sdir, 'node_modules', 'array-flatten', 'package.json')))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npmsw_npm_reusability(self):
        # Fetch once with npmsw
        swfile = self.create_shrinkwrap_file({
            'dependencies': {
                'array-flatten': {
                    'version': '1.1.1',
                    'resolved': 'https://registry.npmjs.org/array-flatten/-/array-flatten-1.1.1.tgz',
                    'integrity': 'sha1-ml9pkFGx5wczKPKgCJaLZOopVdI='
                }
            }
        })
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile], self.d)
        fetcher.download()
        # Disable network access
        self.d.setVar('BB_NO_NETWORK', '1')
        # Fetch again with npm
        fetcher = bb.fetch.Fetch(['npm://registry.npmjs.org;package=array-flatten;version=1.1.1'], self.d)
        fetcher.download()
        fetcher.unpack(self.unpackdir)
        self.assertTrue(os.path.exists(os.path.join(self.unpackdir, 'npm', 'package.json')))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npmsw_bad_checksum(self):
        # Try to fetch with bad checksum
        swfile = self.create_shrinkwrap_file({
            'dependencies': {
                'array-flatten': {
                    'version': '1.1.1',
                    'resolved': 'https://registry.npmjs.org/array-flatten/-/array-flatten-1.1.1.tgz',
                    'integrity': 'sha1-gfNEp2hqgLTFKT6P3AsBYMgsBqg='
                }
            }
        })
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile], self.d)
        with self.assertRaises(bb.fetch2.FetchError):
            fetcher.download()
        # Fetch correctly to get a tarball
        swfile = self.create_shrinkwrap_file({
            'dependencies': {
                'array-flatten': {
                    'version': '1.1.1',
                    'resolved': 'https://registry.npmjs.org/array-flatten/-/array-flatten-1.1.1.tgz',
                    'integrity': 'sha1-ml9pkFGx5wczKPKgCJaLZOopVdI='
                }
            }
        })
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile], self.d)
        fetcher.download()
        localpath = os.path.join(self.dldir, 'npm2', 'array-flatten-1.1.1.tgz')
        self.assertTrue(os.path.exists(localpath))
        # Modify the tarball
        bad = b'bad checksum'
        with open(localpath, 'wb') as f:
            f.write(bad)
        # Verify that the tarball is fetched again
        fetcher.download()
        badsum = hashlib.sha1(bad).hexdigest()
        self.assertTrue(os.path.exists(localpath + '_bad-checksum_' + badsum))
        self.assertTrue(os.path.exists(localpath))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npmsw_premirrors(self):
        # Fetch once to get a tarball
        fetcher = bb.fetch.Fetch(['npm://registry.npmjs.org;package=array-flatten;version=1.1.1'], self.d)
        ud = fetcher.ud[fetcher.urls[0]]
        fetcher.download()
        self.assertTrue(os.path.exists(ud.localpath))
        # Setup the mirror
        mirrordir = os.path.join(self.tempdir, 'mirror')
        bb.utils.mkdirhier(mirrordir)
        os.replace(ud.localpath, os.path.join(mirrordir, os.path.basename(ud.localpath)))
        self.d.setVar('PREMIRRORS', 'https?$://.*/.* file://%s/\n' % mirrordir)
        self.d.setVar('BB_FETCH_PREMIRRORONLY', '1')
        # Fetch again
        self.assertFalse(os.path.exists(ud.localpath))
        swfile = self.create_shrinkwrap_file({
            'dependencies': {
                'array-flatten': {
                    'version': '1.1.1',
                    'resolved': 'https://registry.npmjs.org/array-flatten/-/array-flatten-1.1.1.tgz',
                    'integrity': 'sha1-ml9pkFGx5wczKPKgCJaLZOopVdI='
                }
            }
        })
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile], self.d)
        fetcher.download()
        self.assertTrue(os.path.exists(ud.localpath))

    @skipIfNoNpm()
    @skipIfNoNetwork()
    def test_npmsw_mirrors(self):
        # Fetch once to get a tarball
        fetcher = bb.fetch.Fetch(['npm://registry.npmjs.org;package=array-flatten;version=1.1.1'], self.d)
        ud = fetcher.ud[fetcher.urls[0]]
        fetcher.download()
        self.assertTrue(os.path.exists(ud.localpath))
        # Setup the mirror
        mirrordir = os.path.join(self.tempdir, 'mirror')
        bb.utils.mkdirhier(mirrordir)
        os.replace(ud.localpath, os.path.join(mirrordir, os.path.basename(ud.localpath)))
        self.d.setVar('MIRRORS', 'https?$://.*/.* file://%s/\n' % mirrordir)
        # Fetch again with invalid url
        self.assertFalse(os.path.exists(ud.localpath))
        swfile = self.create_shrinkwrap_file({
            'dependencies': {
                'array-flatten': {
                    'version': '1.1.1',
                    'resolved': 'https://invalid',
                    'integrity': 'sha1-ml9pkFGx5wczKPKgCJaLZOopVdI='
                }
            }
        })
        fetcher = bb.fetch.Fetch(['npmsw://' + swfile], self.d)
        fetcher.download()
        self.assertTrue(os.path.exists(ud.localpath))
