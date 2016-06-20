#!/usr/bin/env python

# generate Python Manifest for the OpenEmbedded build system
# (C) 2002-2010 Michael 'Mickey' Lauer <mlauer@vanille-media.de>
# (C) 2007 Jeremy Laine
# licensed under MIT, see COPYING.MIT
#
# June 22, 2011 -- Mark Hatle <mark.hatle@windriver.com>
#  * Updated to no longer generate special -dbg package, instead use the
#    single system -dbg
#  * Update version with ".1" to indicate this change

import os
import sys
import time

VERSION = "2.7.2"

__author__ = "Michael 'Mickey' Lauer <mlauer@vanille-media.de>"
__version__ = "20110222.2"

class MakefileMaker:

    def __init__( self, outfile ):
        """initialize"""
        self.packages = {}
        self.targetPrefix = "${libdir}/python%s/" % VERSION[:3]
        self.output = outfile
        self.out( """
# WARNING: This file is AUTO GENERATED: Manual edits will be lost next time I regenerate the file.
# Generator: '%s' Version %s (C) 2002-2010 Michael 'Mickey' Lauer <mlauer@vanille-media.de>
# Visit the Python for Embedded Systems Site => http://www.Vanille.de/projects/python.spy
""" % ( sys.argv[0], __version__ ) )

    #
    # helper functions
    #

    def out( self, data ):
        """print a line to the output file"""
        self.output.write( "%s\n" % data )

    def setPrefix( self, targetPrefix ):
        """set a file prefix for addPackage files"""
        self.targetPrefix = targetPrefix

    def doProlog( self ):
        self.out( """ """ )
        self.out( "" )

    def addPackage( self, name, description, dependencies, filenames ):
        """add a package to the Makefile"""
        if type( filenames ) == type( "" ):
            filenames = filenames.split()
        fullFilenames = []
        for filename in filenames:
            if filename[0] != "$":
                fullFilenames.append( "%s%s" % ( self.targetPrefix, filename ) )
            else:
                fullFilenames.append( filename )
        self.packages[name] = description, dependencies, fullFilenames

    def doBody( self ):
        """generate body of Makefile"""

        global VERSION

        #
        # generate provides line
        #

        provideLine = 'PROVIDES+="'
        for name in sorted(self.packages):
            provideLine += "%s " % name
        provideLine += '"'

        self.out( provideLine )
        self.out( "" )

        #
        # generate package line
        #

        packageLine = 'PACKAGES="${PN}-dbg '
        for name in sorted(self.packages):
            if name.startswith("${PN}-distutils"):
                if name == "${PN}-distutils":
                    packageLine += "%s-staticdev %s " % (name, name)
            elif name != '${PN}-dbg':
                packageLine += "%s " % name
        packageLine += '${PN}-modules"'

        self.out( packageLine )
        self.out( "" )

        #
        # generate package variables
        #

        for name, data in sorted(self.packages.iteritems()):
            desc, deps, files = data

            #
            # write out the description, revision and dependencies
            #
            self.out( 'SUMMARY_%s="%s"' % ( name, desc ) )
            self.out( 'RDEPENDS_%s="%s"' % ( name, deps ) )

            line = 'FILES_%s="' % name

            #
            # check which directories to make in the temporary directory
            #

            dirset = {} # if python had a set-datatype this would be sufficient. for now, we're using a dict instead.
            for target in files:
                dirset[os.path.dirname( target )] = True

            #
            # generate which files to copy for the target (-dfR because whole directories are also allowed)
            #

            for target in files:
                line += "%s " % target

            line += '"'
            self.out( line )
            self.out( "" )

        self.out( 'SUMMARY_${PN}-modules="All Python modules"' )
        line = 'RDEPENDS_${PN}-modules="'

        for name, data in sorted(self.packages.iteritems()):
            if name not in ['${PN}-dev', '${PN}-distutils-staticdev']:
                line += "%s " % name

        self.out( "%s \"" % line )
        self.out( 'ALLOW_EMPTY_${PN}-modules = "1"' )

    def doEpilog( self ):
        self.out( """""" )
        self.out( "" )

    def make( self ):
        self.doProlog()
        self.doBody()
        self.doEpilog()

if __name__ == "__main__":

    if len( sys.argv ) > 1:
        try:
            os.unlink(sys.argv[1])
        except Exception:
            sys.exc_clear()
        outfile = file( sys.argv[1], "w" )
    else:
        outfile = sys.stdout

    m = MakefileMaker( outfile )

    # Add packages here. Only specify dlopen-style library dependencies here, no ldd-style dependencies!
    # Parameters: revision, name, description, dependencies, filenames
    #

    m.addPackage( "${PN}-core", "Python interpreter and core modules", "${PN}-lang ${PN}-re",
    "__future__.* _abcoll.* abc.* ast.* copy.* copy_reg.* ConfigParser.* " +
    "genericpath.* getopt.* linecache.* new.* " +
    "os.* posixpath.* struct.* " +
    "warnings.* site.* stat.* " +
    "UserDict.* UserList.* UserString.* " +
    "lib-dynload/binascii.so lib-dynload/_struct.so lib-dynload/time.so " +
    "lib-dynload/xreadlines.so types.* platform.* ${bindir}/python* "  +
    "_weakrefset.* sysconfig.* _sysconfigdata.* config/Makefile " +
    "${includedir}/python${PYTHON_MAJMIN}/pyconfig*.h " +
    "${libdir}/python${PYTHON_MAJMIN}/sitecustomize.py ")

    m.addPackage( "${PN}-dev", "Python development package", "${PN}-core",
    "${includedir} " +
    "${libdir}/lib*${SOLIBSDEV} " +
    "${libdir}/*.la " +
    "${libdir}/*.a " +
    "${libdir}/*.o " +
    "${libdir}/pkgconfig " +
    "${base_libdir}/*.a " +
    "${base_libdir}/*.o " +
    "${datadir}/aclocal " +
    "${datadir}/pkgconfig " )

    m.addPackage( "${PN}-2to3", "Python automated Python 2 to 3 code translator", "${PN}-core",
    "${bindir}/2to3 lib2to3" ) # package

    m.addPackage( "${PN}-idle", "Python Integrated Development Environment", "${PN}-core ${PN}-tkinter",
    "${bindir}/idle idlelib" ) # package

    m.addPackage( "${PN}-pydoc", "Python interactive help support", "${PN}-core ${PN}-lang ${PN}-stringold ${PN}-re",
    "${bindir}/pydoc pydoc.* pydoc_data" )

    m.addPackage( "${PN}-smtpd", "Python Simple Mail Transport Daemon", "${PN}-core ${PN}-netserver ${PN}-email ${PN}-mime",
    "${bindir}/smtpd.* smtpd.*" )

    m.addPackage( "${PN}-audio", "Python Audio Handling", "${PN}-core",
    "wave.* chunk.* sndhdr.* lib-dynload/ossaudiodev.so lib-dynload/audioop.so audiodev.* sunaudio.* sunau.* toaiff.*" )

    m.addPackage( "${PN}-bsddb", "Python bindings for the Berkeley Database", "${PN}-core",
    "bsddb lib-dynload/_bsddb.so" ) # package

    m.addPackage( "${PN}-codecs", "Python codecs, encodings & i18n support", "${PN}-core ${PN}-lang",
    "codecs.* encodings gettext.* locale.* lib-dynload/_locale.so lib-dynload/_codecs* lib-dynload/_multibytecodec.so lib-dynload/unicodedata.so stringprep.* xdrlib.*" )

    m.addPackage( "${PN}-compile", "Python bytecode compilation support", "${PN}-core",
    "py_compile.* compileall.*" )

    m.addPackage( "${PN}-compiler", "Python compiler support", "${PN}-core",
    "compiler" ) # package

    m.addPackage( "${PN}-compression", "Python high-level compression support", "${PN}-core ${PN}-zlib",
    "gzip.* zipfile.* tarfile.* lib-dynload/bz2.so" )

    m.addPackage( "${PN}-crypt", "Python basic cryptographic and hashing support", "${PN}-core",
    "hashlib.* md5.* sha.* lib-dynload/crypt.so lib-dynload/_hashlib.so lib-dynload/_sha256.so lib-dynload/_sha512.so" )

    m.addPackage( "${PN}-textutils", "Python option parsing, text wrapping and CSV support", "${PN}-core ${PN}-io ${PN}-re ${PN}-stringold",
    "lib-dynload/_csv.so csv.* optparse.* textwrap.*" )

    m.addPackage( "${PN}-curses", "Python curses support", "${PN}-core",
    "curses lib-dynload/_curses.so lib-dynload/_curses_panel.so" ) # directory + low level module

    m.addPackage( "${PN}-ctypes", "Python C types support", "${PN}-core",
    "ctypes lib-dynload/_ctypes.so lib-dynload/_ctypes_test.so" ) # directory + low level module

    m.addPackage( "${PN}-datetime", "Python calendar and time support", "${PN}-core ${PN}-codecs",
    "_strptime.* calendar.* lib-dynload/datetime.so" )

    m.addPackage( "${PN}-db", "Python file-based database support", "${PN}-core",
    "anydbm.* dumbdbm.* whichdb.* " )

    m.addPackage( "${PN}-debugger", "Python debugger", "${PN}-core ${PN}-io ${PN}-lang ${PN}-re ${PN}-stringold ${PN}-shell ${PN}-pprint",
    "bdb.* pdb.*" )

    m.addPackage( "${PN}-difflib", "Python helpers for computing deltas between objects", "${PN}-lang ${PN}-re",
    "difflib.*" )

    m.addPackage( "${PN}-distutils-staticdev", "Python distribution utilities (static libraries)", "${PN}-distutils",
    "config/lib*.a" ) # package

    m.addPackage( "${PN}-distutils", "Python Distribution Utilities", "${PN}-core ${PN}-email",
    "config distutils" ) # package

    m.addPackage( "${PN}-doctest", "Python framework for running examples in docstrings", "${PN}-core ${PN}-lang ${PN}-io ${PN}-re ${PN}-unittest ${PN}-debugger ${PN}-difflib",
    "doctest.*" )

    m.addPackage( "${PN}-email", "Python email support", "${PN}-core ${PN}-io ${PN}-re ${PN}-mime ${PN}-audio ${PN}-image ${PN}-netclient",
    "imaplib.* email" ) # package

    m.addPackage( "${PN}-fcntl", "Python's fcntl interface", "${PN}-core",
    "lib-dynload/fcntl.so" )

    m.addPackage( "${PN}-hotshot", "Python hotshot performance profiler", "${PN}-core",
    "hotshot lib-dynload/_hotshot.so" )

    m.addPackage( "${PN}-html", "Python HTML processing support", "${PN}-core",
    "formatter.* htmlentitydefs.* htmllib.* markupbase.* sgmllib.* HTMLParser.* " )

    m.addPackage( "${PN}-importlib", "Python import implementation library", "${PN}-core",
    "importlib" )

    m.addPackage( "${PN}-gdbm", "Python GNU database support", "${PN}-core",
    "lib-dynload/gdbm.so" )

    m.addPackage( "${PN}-image", "Python graphical image handling", "${PN}-core",
    "colorsys.* imghdr.* lib-dynload/imageop.so lib-dynload/rgbimg.so" )

    m.addPackage( "${PN}-io", "Python low-level I/O", "${PN}-core ${PN}-math ${PN}-textutils ${PN}-netclient ${PN}-contextlib",
    "lib-dynload/_socket.so lib-dynload/_io.so lib-dynload/_ssl.so lib-dynload/select.so lib-dynload/termios.so lib-dynload/cStringIO.so " +
    "pipes.* socket.* ssl.* tempfile.* StringIO.* io.* _pyio.*" )

    m.addPackage( "${PN}-json", "Python JSON support", "${PN}-core ${PN}-math ${PN}-re ${PN}-codecs",
    "json lib-dynload/_json.so" ) # package

    m.addPackage( "${PN}-lang", "Python low-level language support", "${PN}-core",
    "lib-dynload/_bisect.so lib-dynload/_collections.so lib-dynload/_heapq.so lib-dynload/_weakref.so lib-dynload/_functools.so " +
    "lib-dynload/array.so lib-dynload/itertools.so lib-dynload/operator.so lib-dynload/parser.so " +
    "atexit.* bisect.* code.* codeop.* collections.* dis.* functools.* heapq.* inspect.* keyword.* opcode.* symbol.* repr.* token.* " +
    "tokenize.* traceback.* weakref.*" )

    m.addPackage( "${PN}-logging", "Python logging support", "${PN}-core ${PN}-io ${PN}-lang ${PN}-pickle ${PN}-stringold",
    "logging" ) # package

    m.addPackage( "${PN}-mailbox", "Python mailbox format support", "${PN}-core ${PN}-mime",
    "mailbox.*" )

    m.addPackage( "${PN}-math", "Python math support", "${PN}-core ${PN}-crypt",
    "lib-dynload/cmath.so lib-dynload/math.so lib-dynload/_random.so random.* sets.*" )

    m.addPackage( "${PN}-mime", "Python MIME handling APIs", "${PN}-core ${PN}-io",
    "mimetools.* uu.* quopri.* rfc822.* MimeWriter.*" )

    m.addPackage( "${PN}-mmap", "Python memory-mapped file support", "${PN}-core ${PN}-io",
    "lib-dynload/mmap.so " )

    m.addPackage( "${PN}-multiprocessing", "Python multiprocessing support", "${PN}-core ${PN}-io ${PN}-lang ${PN}-pickle ${PN}-threading ${PN}-ctypes ${PN}-mmap",
    "lib-dynload/_multiprocessing.so multiprocessing" ) # package

    m.addPackage( "${PN}-netclient", "Python Internet Protocol clients", "${PN}-core ${PN}-crypt ${PN}-datetime ${PN}-io ${PN}-lang ${PN}-logging ${PN}-mime",
    "*Cookie*.* " +
    "base64.* cookielib.* ftplib.* gopherlib.* hmac.* httplib.* mimetypes.* nntplib.* poplib.* smtplib.* telnetlib.* urllib.* urllib2.* urlparse.* uuid.* rfc822.* mimetools.*" )

    m.addPackage( "${PN}-netserver", "Python Internet Protocol servers", "${PN}-core ${PN}-netclient ${PN}-shell ${PN}-threading",
    "cgi.* *HTTPServer.* SocketServer.*" )

    m.addPackage( "${PN}-numbers", "Python number APIs", "${PN}-core ${PN}-lang ${PN}-re",
    "decimal.* fractions.* numbers.*" )

    m.addPackage( "${PN}-pickle", "Python serialisation/persistence support", "${PN}-core ${PN}-codecs ${PN}-io ${PN}-re",
    "pickle.* shelve.* lib-dynload/cPickle.so pickletools.*" )

    m.addPackage( "${PN}-pkgutil", "Python package extension utility support", "${PN}-core",
    "pkgutil.*")

    m.addPackage( "${PN}-plistlib", "Generate and parse Mac OS X .plist files", "${PN}-core ${PN}-datetime ${PN}-io",
    "plistlib.*")

    m.addPackage( "${PN}-pprint", "Python pretty-print support", "${PN}-core ${PN}-io",
    "pprint.*" )

    m.addPackage( "${PN}-profile", "Python basic performance profiling support", "${PN}-core ${PN}-textutils",
    "profile.* pstats.* cProfile.* lib-dynload/_lsprof.so" )

    m.addPackage( "${PN}-re", "Python Regular Expression APIs", "${PN}-core",
    "re.* sre.* sre_compile.* sre_constants* sre_parse.*" ) # _sre is builtin

    m.addPackage( "${PN}-readline", "Python readline support", "${PN}-core",
    "lib-dynload/readline.so rlcompleter.*" )

    m.addPackage( "${PN}-resource", "Python resource control interface", "${PN}-core",
    "lib-dynload/resource.so" )

    m.addPackage( "${PN}-shell", "Python shell-like functionality", "${PN}-core ${PN}-re",
    "cmd.* commands.* dircache.* fnmatch.* glob.* popen2.* shlex.* shutil.*" )

    m.addPackage( "${PN}-robotparser", "Python robots.txt parser", "${PN}-core ${PN}-netclient",
    "robotparser.*")

    m.addPackage( "${PN}-subprocess", "Python subprocess support", "${PN}-core ${PN}-io ${PN}-re ${PN}-fcntl ${PN}-pickle",
    "subprocess.*" )

    m.addPackage( "${PN}-sqlite3", "Python Sqlite3 database support", "${PN}-core ${PN}-datetime ${PN}-lang ${PN}-crypt ${PN}-io ${PN}-threading ${PN}-zlib",
    "lib-dynload/_sqlite3.so sqlite3/dbapi2.* sqlite3/__init__.* sqlite3/dump.*" )

    m.addPackage( "${PN}-sqlite3-tests", "Python Sqlite3 database support tests", "${PN}-core ${PN}-sqlite3",
    "sqlite3/test" )

    m.addPackage( "${PN}-stringold", "Python string APIs [deprecated]", "${PN}-core ${PN}-re",
    "lib-dynload/strop.so string.* stringold.*" )

    m.addPackage( "${PN}-syslog", "Python syslog interface", "${PN}-core",
    "lib-dynload/syslog.so" )

    m.addPackage( "${PN}-terminal", "Python terminal controlling support", "${PN}-core ${PN}-io",
    "pty.* tty.*" )

    m.addPackage( "${PN}-tests", "Python tests", "${PN}-core",
    "test" ) # package

    m.addPackage( "${PN}-threading", "Python threading & synchronization support", "${PN}-core ${PN}-lang",
    "_threading_local.* dummy_thread.* dummy_threading.* mutex.* threading.* Queue.*" )

    m.addPackage( "${PN}-tkinter", "Python Tcl/Tk bindings", "${PN}-core",
    "lib-dynload/_tkinter.so lib-tk" ) # package

    m.addPackage( "${PN}-unittest", "Python unit testing framework", "${PN}-core ${PN}-stringold ${PN}-lang ${PN}-io ${PN}-difflib ${PN}-pprint ${PN}-shell",
    "unittest/" )

    m.addPackage( "${PN}-unixadmin", "Python Unix administration support", "${PN}-core",
    "lib-dynload/nis.so lib-dynload/grp.so lib-dynload/pwd.so getpass.*" )

    m.addPackage( "${PN}-xml", "Python basic XML support", "${PN}-core ${PN}-re",
    "lib-dynload/_elementtree.so lib-dynload/pyexpat.so xml xmllib.*" ) # package

    m.addPackage( "${PN}-xmlrpc", "Python XML-RPC support", "${PN}-core ${PN}-xml ${PN}-netserver ${PN}-lang",
    "xmlrpclib.* SimpleXMLRPCServer.* DocXMLRPCServer.*" )

    m.addPackage( "${PN}-zlib", "Python zlib compression support", "${PN}-core",
    "lib-dynload/zlib.so" )

    m.addPackage( "${PN}-mailbox", "Python mailbox format support", "${PN}-core ${PN}-mime",
    "mailbox.*" )

    m.addPackage( "${PN}-argparse", "Python command line argument parser", "${PN}-core ${PN}-codecs ${PN}-textutils",
    "argparse.*" )

    m.addPackage( "${PN}-contextlib", "Python utilities for with-statement" +
    "contexts.", "${PN}-core",
    "${libdir}/python${PYTHON_MAJMIN}/contextlib.*" )

    m.make()
