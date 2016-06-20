# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Tests for the Fetcher (fetch2/)
#
# Copyright (C) 2012 Richard Purdie
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#

import unittest
import tempfile
import subprocess
import os
from bb.fetch2 import URI
from bb.fetch2 import FetchMethod
import bb

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
            'params': {
                'tag': 'V0-99-81',
                'module': 'familiar/dist/ipkg'
            },
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

class FetcherLocalTest(FetcherTest):
    def setUp(self):
        def touch(fn):
            with file(fn, 'a'):
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

class FetcherNetworkTest(FetcherTest):

    if os.environ.get("BB_SKIP_NETTESTS") == "yes":
        print("Unset BB_SKIP_NETTESTS to run network tests")
    else:
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

        def test_fetch_mirror(self):
            self.d.setVar("MIRRORS", "http://.*/.* http://downloads.yoctoproject.org/releases/bitbake")
            fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz"], self.d)
            fetcher.download()
            self.assertEqual(os.path.getsize(self.dldir + "/bitbake-1.0.tar.gz"), 57749)

        def test_fetch_mirror_of_mirror(self):
            self.d.setVar("MIRRORS", "http://.*/.* http://invalid2.yoctoproject.org/ \n http://invalid2.yoctoproject.org/.* http://downloads.yoctoproject.org/releases/bitbake")
            fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz"], self.d)
            fetcher.download()
            self.assertEqual(os.path.getsize(self.dldir + "/bitbake-1.0.tar.gz"), 57749)

        def test_fetch_file_mirror_of_mirror(self):
            self.d.setVar("MIRRORS", "http://.*/.* file:///some1where/ \n file:///some1where/.* file://some2where/ \n file://some2where/.* http://downloads.yoctoproject.org/releases/bitbake")
            fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz"], self.d)
            os.mkdir(self.dldir + "/some2where")
            fetcher.download()
            self.assertEqual(os.path.getsize(self.dldir + "/bitbake-1.0.tar.gz"), 57749)

        def test_fetch_premirror(self):
            self.d.setVar("PREMIRRORS", "http://.*/.* http://downloads.yoctoproject.org/releases/bitbake")
            fetcher = bb.fetch.Fetch(["http://invalid.yoctoproject.org/releases/bitbake/bitbake-1.0.tar.gz"], self.d)
            fetcher.download()
            self.assertEqual(os.path.getsize(self.dldir + "/bitbake-1.0.tar.gz"), 57749)

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

        def test_gitfetch(self):
            url1 = url2 = "git://git.openembedded.org/bitbake"
            self.gitfetcher(url1, url2)

        def test_gitfetch_goodsrcrev(self):
            # SRCREV is set but matches rev= parameter
            url1 = url2 = "git://git.openembedded.org/bitbake;rev=270a05b0b4ba0959fe0624d2a4885d7b70426da5"
            self.gitfetcher(url1, url2)

        def test_gitfetch_badsrcrev(self):
            # SRCREV is set but does not match rev= parameter
            url1 = url2 = "git://git.openembedded.org/bitbake;rev=dead05b0b4ba0959fe0624d2a4885d7b70426da5"
            self.assertRaises(bb.fetch.FetchError, self.gitfetcher, url1, url2)

        def test_gitfetch_tagandrev(self):
            # SRCREV is set but does not match rev= parameter
            url1 = url2 = "git://git.openembedded.org/bitbake;rev=270a05b0b4ba0959fe0624d2a4885d7b70426da5;tag=270a05b0b4ba0959fe0624d2a4885d7b70426da5"
            self.assertRaises(bb.fetch.FetchError, self.gitfetcher, url1, url2)

        def test_gitfetch_premirror(self):
            url1 = "git://git.openembedded.org/bitbake"
            url2 = "git://someserver.org/bitbake"
            self.d.setVar("PREMIRRORS", "git://someserver.org/bitbake git://git.openembedded.org/bitbake \n")
            self.gitfetcher(url1, url2)

        def test_gitfetch_premirror2(self):
            url1 = url2 = "git://someserver.org/bitbake"
            self.d.setVar("PREMIRRORS", "git://someserver.org/bitbake git://git.openembedded.org/bitbake \n")
            self.gitfetcher(url1, url2)

        def test_gitfetch_premirror3(self):
            realurl = "git://git.openembedded.org/bitbake"
            dummyurl = "git://someserver.org/bitbake"
            self.sourcedir = self.unpackdir.replace("unpacked", "sourcemirror.git")
            os.chdir(self.tempdir)
            bb.process.run("git clone %s %s 2> /dev/null" % (realurl, self.sourcedir), shell=True)
            self.d.setVar("PREMIRRORS", "%s git://%s;protocol=file \n" % (dummyurl, self.sourcedir))
            self.gitfetcher(dummyurl, dummyurl)

        def test_git_submodule(self):
            fetcher = bb.fetch.Fetch(["gitsm://git.yoctoproject.org/git-submodule-test;rev=f12e57f2edf0aa534cf1616fa983d165a92b0842"], self.d)
            fetcher.download()
            # Previous cwd has been deleted
            os.chdir(os.path.dirname(self.unpackdir))
            fetcher.unpack(self.unpackdir)


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
       "cvs://anoncvs:anonymous@cvs.handhelds.org/cvs;tag=V0-99-81;module=familiar/dist/ipkg" : ('cvs', 'cvs.handhelds.org', '/cvs', 'anoncvs', 'anonymous', {'tag': 'V0-99-81', 'module': 'familiar/dist/ipkg'}),
       "git://git.openembedded.org/bitbake;branch=@foo" : ('git', 'git.openembedded.org', '/bitbake', '', '', {'branch': '@foo'}),
       "file://somelocation;someparam=1": ('file', '', 'somelocation', '', '', {'someparam': '1'}),
    }
    # we require a pathname to encodeurl but users can still pass such urls to 
    # decodeurl and we need to handle them
    decodedata = datatable.copy()
    decodedata.update({
       "http://somesite.net;someparam=1": ('http', 'somesite.net', '', '', '', {'someparam': '1'}),
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
        ("mx-1.0", "git://github.com/clutter-project/mx.git;branch=mx-1.4", "9b1db6b8060bd00b121a692f942404a24ae2960f", "")
            : "1.99.4",
        # version pattern "vX.Y"
        ("mtd-utils", "git://git.infradead.org/mtd-utils.git", "ca39eb1d98e736109c64ff9c1aa2a6ecca222d8f", "")
            : "1.5.0",
        # version pattern "pkg_name-X.Y"
        ("presentproto", "git://anongit.freedesktop.org/git/xorg/proto/presentproto", "24f3a56e541b0a9e6c6ee76081f441221a120ef9", "")
            : "1.0",
        # version pattern "pkg_name-vX.Y.Z"
        ("dtc", "git://git.qemu.org/dtc.git", "65cc4d2748a2c2e6f27f1cf39e07a5dbabd80ebf", "")
            : "1.4.0",
        # combination version pattern
        ("sysprof", "git://git.gnome.org/sysprof", "cd44ee6644c3641507fb53b8a2a69137f2971219", "")
            : "1.2.0",
        ("u-boot-mkimage", "git://git.denx.de/u-boot.git;branch=master;protocol=git", "62c175fbb8a0f9a926c88294ea9f7e88eb898f6c", "")
            : "2014.01",
        # version pattern "yyyymmdd"
        ("mobile-broadband-provider-info", "git://git.gnome.org/mobile-broadband-provider-info", "4ed19e11c2975105b71b956440acdb25d46a347d", "")
            : "20120614",
        # packages with a valid UPSTREAM_CHECK_GITTAGREGEX
        ("xf86-video-omap", "git://anongit.freedesktop.org/xorg/driver/xf86-video-omap", "ae0394e687f1a77e966cf72f895da91840dffb8f", "(?P<pver>(\d+\.(\d\.?)*))")
            : "0.4.3",
        ("build-appliance-image", "git://git.yoctoproject.org/poky", "b37dd451a52622d5b570183a81583cc34c2ff555", "(?P<pver>(([0-9][\.|_]?)+[0-9]))")
            : "11.0.0",
        ("chkconfig-alternatives-native", "git://github.com/kergoth/chkconfig;branch=sysroot", "cd437ecbd8986c894442f8fce1e0061e20f04dee", "chkconfig\-(?P<pver>((\d+[\.\-_]*)+))")
            : "1.3.59",
        ("remake", "git://github.com/rocky/remake.git", "f05508e521987c8494c92d9c2871aec46307d51d", "(?P<pver>(\d+\.(\d+\.)*\d*(\+dbg\d+(\.\d+)*)*))")
            : "3.82+dbg0.9",
    }

    test_wget_uris = {
        # packages with versions inside directory name
        ("util-linux", "http://kernel.org/pub/linux/utils/util-linux/v2.23/util-linux-2.24.2.tar.bz2", "", "")
            : "2.24.2",
        ("enchant", "http://www.abisource.com/downloads/enchant/1.6.0/enchant-1.6.0.tar.gz", "", "")
            : "1.6.0",
        ("cmake", "http://www.cmake.org/files/v2.8/cmake-2.8.12.1.tar.gz", "", "")
            : "2.8.12.1",
        # packages with versions only in current directory
        ("eglic", "http://downloads.yoctoproject.org/releases/eglibc/eglibc-2.18-svnr23787.tar.bz2", "", "")
            : "2.19",
        ("gnu-config", "http://downloads.yoctoproject.org/releases/gnu-config/gnu-config-20120814.tar.bz2", "", "")
            : "20120814",
        # packages with "99" in the name of possible version
        ("pulseaudio", "http://freedesktop.org/software/pulseaudio/releases/pulseaudio-4.0.tar.xz", "", "")
            : "5.0",
        ("xserver-xorg", "http://xorg.freedesktop.org/releases/individual/xserver/xorg-server-1.15.1.tar.bz2", "", "")
            : "1.15.1",
        # packages with valid UPSTREAM_CHECK_URI and UPSTREAM_CHECK_REGEX
        ("cups", "http://www.cups.org/software/1.7.2/cups-1.7.2-source.tar.bz2", "https://github.com/apple/cups/releases", "(?P<name>cups\-)(?P<pver>((\d+[\.\-_]*)+))\-source\.tar\.gz")
            : "2.0.0",
        ("db", "http://download.oracle.com/berkeley-db/db-5.3.21.tar.gz", "http://www.oracle.com/technetwork/products/berkeleydb/downloads/index-082944.html", "http://download.oracle.com/otn/berkeley-db/(?P<name>db-)(?P<pver>((\d+[\.\-_]*)+))\.tar\.gz")
            : "6.1.19",
    }
    if os.environ.get("BB_SKIP_NETTESTS") == "yes":
        print("Unset BB_SKIP_NETTESTS to run network tests")
    else:
        def test_git_latest_versionstring(self):
            for k, v in self.test_git_uris.items():
                self.d.setVar("PN", k[0])
                self.d.setVar("SRCREV", k[2])
                self.d.setVar("UPSTREAM_CHECK_GITTAGREGEX", k[3])
                ud = bb.fetch2.FetchData(k[1], self.d)
                pupver= ud.method.latest_versionstring(ud, self.d)
                verstring = pupver[0]
                r = bb.utils.vercmp_string(v, verstring)
                self.assertTrue(r == -1 or r == 0, msg="Package %s, version: %s <= %s" % (k[0], v, verstring))

        def test_wget_latest_versionstring(self):
            for k, v in self.test_wget_uris.items():
                self.d.setVar("PN", k[0])
                self.d.setVar("UPSTREAM_CHECK_URI", k[2])
                self.d.setVar("UPSTREAM_CHECK_REGEX", k[3])
                ud = bb.fetch2.FetchData(k[1], self.d)
                pupver = ud.method.latest_versionstring(ud, self.d)
                verstring = pupver[0]
                r = bb.utils.vercmp_string(v, verstring)
                self.assertTrue(r == -1 or r == 0, msg="Package %s, version: %s <= %s" % (k[0], v, verstring))


class FetchCheckStatusTest(FetcherTest):
    test_wget_uris = ["http://www.cups.org/software/1.7.2/cups-1.7.2-source.tar.bz2",
                      "http://www.cups.org/software/ipptool/ipptool-20130731-linux-ubuntu-i686.tar.gz",
                      "http://www.cups.org/",
                      "http://downloads.yoctoproject.org/releases/sato/sato-engine-0.1.tar.gz",
                      "http://downloads.yoctoproject.org/releases/sato/sato-engine-0.2.tar.gz",
                      "http://downloads.yoctoproject.org/releases/sato/sato-engine-0.3.tar.gz",
                      "https://yoctoproject.org/",
                      "https://yoctoproject.org/documentation",
                      "http://downloads.yoctoproject.org/releases/opkg/opkg-0.1.7.tar.gz",
                      "http://downloads.yoctoproject.org/releases/opkg/opkg-0.3.0.tar.gz",
                      "ftp://ftp.gnu.org/gnu/autoconf/autoconf-2.60.tar.gz",
                      "ftp://ftp.gnu.org/gnu/chess/gnuchess-5.08.tar.gz",
                      "ftp://ftp.gnu.org/gnu/gmp/gmp-4.0.tar.gz",
                      # GitHub releases are hosted on Amazon S3, which doesn't support HEAD
                      "https://github.com/kergoth/tslib/releases/download/1.1/tslib-1.1.tar.xz"
                      ]

    if os.environ.get("BB_SKIP_NETTESTS") == "yes":
        print("Unset BB_SKIP_NETTESTS to run network tests")
    else:

        def test_wget_checkstatus(self):
            fetch = bb.fetch2.Fetch(self.test_wget_uris, self.d)
            for u in self.test_wget_uris:
                ud = fetch.ud[u]
                m = ud.method
                ret = m.checkstatus(fetch, ud, self.d)
                self.assertTrue(ret, msg="URI %s, can't check status" % (u))


        def test_wget_checkstatus_connection_cache(self):
            from bb.fetch2 import FetchConnectionCache

            connection_cache = FetchConnectionCache()
            fetch = bb.fetch2.Fetch(self.test_wget_uris, self.d,
                        connection_cache = connection_cache)

            for u in self.test_wget_uris:
                ud = fetch.ud[u]
                m = ud.method
                ret = m.checkstatus(fetch, ud, self.d)
                self.assertTrue(ret, msg="URI %s, can't check status" % (u))

            connection_cache.close_connections()
