
# WARNING: This file is AUTO GENERATED: Manual edits will be lost next time I regenerate the file.
# Generator: '../../../scripts/contrib/python/generate-manifest-3.5.py' Version 20140131 (C) 2002-2010 Michael 'Mickey' Lauer <mlauer@vanille-media.de>
# Visit the Python for Embedded Systems Site => http://www.Vanille.de/projects/python.spy

 

PROVIDES+="${PN}-2to3 ${PN}-argparse ${PN}-asyncio ${PN}-audio ${PN}-codecs ${PN}-compile ${PN}-compression ${PN}-core ${PN}-crypt ${PN}-ctypes ${PN}-curses ${PN}-datetime ${PN}-db ${PN}-debugger ${PN}-dev ${PN}-difflib ${PN}-distutils ${PN}-distutils-staticdev ${PN}-doctest ${PN}-email ${PN}-enum ${PN}-fcntl ${PN}-gdbm ${PN}-html ${PN}-idle ${PN}-image ${PN}-importlib ${PN}-io ${PN}-json ${PN}-lang ${PN}-logging ${PN}-mailbox ${PN}-math ${PN}-mime ${PN}-mmap ${PN}-multiprocessing ${PN}-netclient ${PN}-netserver ${PN}-numbers ${PN}-pickle ${PN}-pkgutil ${PN}-pprint ${PN}-profile ${PN}-pydoc ${PN}-re ${PN}-readline ${PN}-reprlib ${PN}-resource ${PN}-selectors ${PN}-shell ${PN}-signal ${PN}-smtpd ${PN}-sqlite3 ${PN}-sqlite3-tests ${PN}-stringold ${PN}-subprocess ${PN}-syslog ${PN}-terminal ${PN}-tests ${PN}-textutils ${PN}-threading ${PN}-tkinter ${PN}-unittest ${PN}-unixadmin ${PN}-xml ${PN}-xmlrpc "

PACKAGES="${PN}-dbg ${PN}-2to3 ${PN}-argparse ${PN}-asyncio ${PN}-audio ${PN}-codecs ${PN}-compile ${PN}-compression ${PN}-core ${PN}-crypt ${PN}-ctypes ${PN}-curses ${PN}-datetime ${PN}-db ${PN}-debugger ${PN}-dev ${PN}-difflib ${PN}-distutils-staticdev ${PN}-distutils ${PN}-doctest ${PN}-email ${PN}-enum ${PN}-fcntl ${PN}-gdbm ${PN}-html ${PN}-idle ${PN}-image ${PN}-importlib ${PN}-io ${PN}-json ${PN}-lang ${PN}-logging ${PN}-mailbox ${PN}-math ${PN}-mime ${PN}-mmap ${PN}-multiprocessing ${PN}-netclient ${PN}-netserver ${PN}-numbers ${PN}-pickle ${PN}-pkgutil ${PN}-pprint ${PN}-profile ${PN}-pydoc ${PN}-re ${PN}-readline ${PN}-reprlib ${PN}-resource ${PN}-selectors ${PN}-shell ${PN}-signal ${PN}-smtpd ${PN}-sqlite3 ${PN}-sqlite3-tests ${PN}-stringold ${PN}-subprocess ${PN}-syslog ${PN}-terminal ${PN}-tests ${PN}-textutils ${PN}-threading ${PN}-tkinter ${PN}-unittest ${PN}-unixadmin ${PN}-xml ${PN}-xmlrpc ${PN}-modules"

SUMMARY_${PN}-2to3="Python automated Python 2 to 3 code translator"
RDEPENDS_${PN}-2to3="${PN}-core"
FILES_${PN}-2to3="${libdir}/python3.5/lib2to3 "

SUMMARY_${PN}-argparse="Python command line argument parser"
RDEPENDS_${PN}-argparse="${PN}-core ${PN}-codecs ${PN}-textutils"
FILES_${PN}-argparse="${libdir}/python3.5/argparse.* "

SUMMARY_${PN}-asyncio="Python Asynchronous I/O, event loop, coroutines and tasks"
RDEPENDS_${PN}-asyncio="${PN}-core"
FILES_${PN}-asyncio="${libdir}/python3.5/asyncio "

SUMMARY_${PN}-audio="Python Audio Handling"
RDEPENDS_${PN}-audio="${PN}-core"
FILES_${PN}-audio="${libdir}/python3.5/wave.* ${libdir}/python3.5/chunk.* ${libdir}/python3.5/sndhdr.* ${libdir}/python3.5/lib-dynload/ossaudiodev.*.so ${libdir}/python3.5/lib-dynload/audioop.*.so ${libdir}/python3.5/audiodev.* ${libdir}/python3.5/sunaudio.* ${libdir}/python3.5/sunau.* ${libdir}/python3.5/toaiff.* "

SUMMARY_${PN}-codecs="Python codecs, encodings & i18n support"
RDEPENDS_${PN}-codecs="${PN}-core ${PN}-lang"
FILES_${PN}-codecs="${libdir}/python3.5/codecs.* ${libdir}/python3.5/encodings ${libdir}/python3.5/gettext.* ${libdir}/python3.5/locale.* ${libdir}/python3.5/lib-dynload/_locale.*.so ${libdir}/python3.5/lib-dynload/_codecs* ${libdir}/python3.5/lib-dynload/_multibytecodec.*.so ${libdir}/python3.5/lib-dynload/unicodedata.*.so ${libdir}/python3.5/stringprep.* ${libdir}/python3.5/xdrlib.* "

SUMMARY_${PN}-compile="Python bytecode compilation support"
RDEPENDS_${PN}-compile="${PN}-core"
FILES_${PN}-compile="${libdir}/python3.5/py_compile.* ${libdir}/python3.5/compileall.* "

SUMMARY_${PN}-compression="Python high-level compression support"
RDEPENDS_${PN}-compression="${PN}-core ${PN}-codecs ${PN}-importlib ${PN}-threading ${PN}-shell"
FILES_${PN}-compression="${libdir}/python3.5/gzip.* ${libdir}/python3.5/zipfile.* ${libdir}/python3.5/tarfile.* ${libdir}/python3.5/lib-dynload/bz2.*.so ${libdir}/python3.5/lib-dynload/zlib.*.so "

SUMMARY_${PN}-core="Python interpreter and core modules"
RDEPENDS_${PN}-core="${PN}-lang ${PN}-re ${PN}-reprlib ${PN}-codecs ${PN}-io ${PN}-math"
FILES_${PN}-core="${libdir}/python3.5/__future__.* ${libdir}/python3.5/_abcoll.* ${libdir}/python3.5/abc.* ${libdir}/python3.5/ast.* ${libdir}/python3.5/copy.* ${libdir}/python3.5/copyreg.* ${libdir}/python3.5/configparser.* ${libdir}/python3.5/genericpath.* ${libdir}/python3.5/getopt.* ${libdir}/python3.5/linecache.* ${libdir}/python3.5/new.* ${libdir}/python3.5/os.* ${libdir}/python3.5/posixpath.* ${libdir}/python3.5/struct.* ${libdir}/python3.5/warnings.* ${libdir}/python3.5/site.* ${libdir}/python3.5/stat.* ${libdir}/python3.5/UserDict.* ${libdir}/python3.5/UserList.* ${libdir}/python3.5/UserString.* ${libdir}/python3.5/lib-dynload/binascii.*.so ${libdir}/python3.5/lib-dynload/_struct.*.so ${libdir}/python3.5/lib-dynload/time.*.so ${libdir}/python3.5/lib-dynload/xreadlines.*.so ${libdir}/python3.5/types.* ${libdir}/python3.5/platform.* ${bindir}/python* ${libdir}/python3.5/_weakrefset.* ${libdir}/python3.5/sysconfig.* ${libdir}/python3.5/_sysconfigdata.* ${libdir}/python3.5/config/Makefile ${includedir}/python${PYTHON_BINABI}/pyconfig*.h ${libdir}/python${PYTHON_MAJMIN}/collections ${libdir}/python${PYTHON_MAJMIN}/_collections_abc.* ${libdir}/python${PYTHON_MAJMIN}/_sitebuiltins.* ${libdir}/python${PYTHON_MAJMIN}/sitecustomize.py "

SUMMARY_${PN}-crypt="Python basic cryptographic and hashing support"
RDEPENDS_${PN}-crypt="${PN}-core"
FILES_${PN}-crypt="${libdir}/python3.5/hashlib.* ${libdir}/python3.5/md5.* ${libdir}/python3.5/sha.* ${libdir}/python3.5/lib-dynload/crypt.*.so ${libdir}/python3.5/lib-dynload/_hashlib.*.so ${libdir}/python3.5/lib-dynload/_sha256.*.so ${libdir}/python3.5/lib-dynload/_sha512.*.so "

SUMMARY_${PN}-ctypes="Python C types support"
RDEPENDS_${PN}-ctypes="${PN}-core ${PN}-subprocess"
FILES_${PN}-ctypes="${libdir}/python3.5/ctypes ${libdir}/python3.5/lib-dynload/_ctypes.*.so ${libdir}/python3.5/lib-dynload/_ctypes_test.*.so "

SUMMARY_${PN}-curses="Python curses support"
RDEPENDS_${PN}-curses="${PN}-core"
FILES_${PN}-curses="${libdir}/python3.5/curses ${libdir}/python3.5/lib-dynload/_curses.*.so ${libdir}/python3.5/lib-dynload/_curses_panel.*.so "

SUMMARY_${PN}-datetime="Python calendar and time support"
RDEPENDS_${PN}-datetime="${PN}-core ${PN}-codecs"
FILES_${PN}-datetime="${libdir}/python3.5/_strptime.* ${libdir}/python3.5/calendar.* ${libdir}/python3.5/datetime.* ${libdir}/python3.5/lib-dynload/_datetime.*.so "

SUMMARY_${PN}-db="Python file-based database support"
RDEPENDS_${PN}-db="${PN}-core"
FILES_${PN}-db="${libdir}/python3.5/anydbm.* ${libdir}/python3.5/dumbdbm.* ${libdir}/python3.5/whichdb.* ${libdir}/python3.5/dbm ${libdir}/python3.5/lib-dynload/_dbm.*.so "

SUMMARY_${PN}-debugger="Python debugger"
RDEPENDS_${PN}-debugger="${PN}-core ${PN}-io ${PN}-lang ${PN}-re ${PN}-stringold ${PN}-shell ${PN}-pprint ${PN}-importlib ${PN}-pkgutil"
FILES_${PN}-debugger="${libdir}/python3.5/bdb.* ${libdir}/python3.5/pdb.* "

SUMMARY_${PN}-dev="Python development package"
RDEPENDS_${PN}-dev="${PN}-core"
FILES_${PN}-dev="${includedir} ${libdir}/lib*${SOLIBSDEV} ${libdir}/*.la ${libdir}/*.a ${libdir}/*.o ${libdir}/pkgconfig ${base_libdir}/*.a ${base_libdir}/*.o ${datadir}/aclocal ${datadir}/pkgconfig "

SUMMARY_${PN}-difflib="Python helpers for computing deltas between objects"
RDEPENDS_${PN}-difflib="${PN}-lang ${PN}-re"
FILES_${PN}-difflib="${libdir}/python3.5/difflib.* "

SUMMARY_${PN}-distutils="Python Distribution Utilities"
RDEPENDS_${PN}-distutils="${PN}-core ${PN}-email"
FILES_${PN}-distutils="${libdir}/python3.5/config ${libdir}/python3.5/distutils "

SUMMARY_${PN}-distutils-staticdev="Python distribution utilities (static libraries)"
RDEPENDS_${PN}-distutils-staticdev="${PN}-distutils"
FILES_${PN}-distutils-staticdev="${libdir}/python3.5/config/lib*.a "

SUMMARY_${PN}-doctest="Python framework for running examples in docstrings"
RDEPENDS_${PN}-doctest="${PN}-core ${PN}-lang ${PN}-io ${PN}-re ${PN}-unittest ${PN}-debugger ${PN}-difflib"
FILES_${PN}-doctest="${libdir}/python3.5/doctest.* "

SUMMARY_${PN}-email="Python email support"
RDEPENDS_${PN}-email="${PN}-core ${PN}-io ${PN}-re ${PN}-mime ${PN}-audio ${PN}-image ${PN}-netclient"
FILES_${PN}-email="${libdir}/python3.5/imaplib.* ${libdir}/python3.5/email "

SUMMARY_${PN}-enum="Python support for enumerations"
RDEPENDS_${PN}-enum="${PN}-core"
FILES_${PN}-enum="${libdir}/python3.5/enum.* "

SUMMARY_${PN}-fcntl="Python's fcntl interface"
RDEPENDS_${PN}-fcntl="${PN}-core"
FILES_${PN}-fcntl="${libdir}/python3.5/lib-dynload/fcntl.*.so "

SUMMARY_${PN}-gdbm="Python GNU database support"
RDEPENDS_${PN}-gdbm="${PN}-core"
FILES_${PN}-gdbm="${libdir}/python3.5/lib-dynload/_gdbm.*.so "

SUMMARY_${PN}-html="Python HTML processing support"
RDEPENDS_${PN}-html="${PN}-core"
FILES_${PN}-html="${libdir}/python3.5/formatter.* ${libdir}/python3.5/htmlentitydefs.* ${libdir}/python3.5/htmllib.* ${libdir}/python3.5/markupbase.* ${libdir}/python3.5/sgmllib.* ${libdir}/python3.5/HTMLParser.* "

SUMMARY_${PN}-idle="Python Integrated Development Environment"
RDEPENDS_${PN}-idle="${PN}-core ${PN}-tkinter"
FILES_${PN}-idle="${bindir}/idle ${libdir}/python3.5/idlelib "

SUMMARY_${PN}-image="Python graphical image handling"
RDEPENDS_${PN}-image="${PN}-core"
FILES_${PN}-image="${libdir}/python3.5/colorsys.* ${libdir}/python3.5/imghdr.* ${libdir}/python3.5/lib-dynload/imageop.*.so ${libdir}/python3.5/lib-dynload/rgbimg.*.so "

SUMMARY_${PN}-importlib="Python import implementation library"
RDEPENDS_${PN}-importlib="${PN}-core ${PN}-lang"
FILES_${PN}-importlib="${libdir}/python3.5/importlib ${libdir}/python3.5/imp.* "

SUMMARY_${PN}-io="Python low-level I/O"
RDEPENDS_${PN}-io="${PN}-core ${PN}-math"
FILES_${PN}-io="${libdir}/python3.5/lib-dynload/_socket.*.so ${libdir}/python3.5/lib-dynload/_io.*.so ${libdir}/python3.5/lib-dynload/_ssl.*.so ${libdir}/python3.5/lib-dynload/select.*.so ${libdir}/python3.5/lib-dynload/termios.*.so ${libdir}/python3.5/lib-dynload/cStringIO.*.so ${libdir}/python3.5/pipes.* ${libdir}/python3.5/socket.* ${libdir}/python3.5/ssl.* ${libdir}/python3.5/tempfile.* ${libdir}/python3.5/StringIO.* ${libdir}/python3.5/io.* ${libdir}/python3.5/_pyio.* "

SUMMARY_${PN}-json="Python JSON support"
RDEPENDS_${PN}-json="${PN}-core ${PN}-math ${PN}-re"
FILES_${PN}-json="${libdir}/python3.5/json ${libdir}/python3.5/lib-dynload/_json.*.so "

SUMMARY_${PN}-lang="Python low-level language support"
RDEPENDS_${PN}-lang="${PN}-core ${PN}-importlib"
FILES_${PN}-lang="${libdir}/python3.5/lib-dynload/_bisect.*.so ${libdir}/python3.5/lib-dynload/_collections.*.so ${libdir}/python3.5/lib-dynload/_heapq.*.so ${libdir}/python3.5/lib-dynload/_weakref.*.so ${libdir}/python3.5/lib-dynload/_functools.*.so ${libdir}/python3.5/lib-dynload/array.*.so ${libdir}/python3.5/lib-dynload/itertools.*.so ${libdir}/python3.5/lib-dynload/operator.*.so ${libdir}/python3.5/lib-dynload/parser.*.so ${libdir}/python3.5/atexit.* ${libdir}/python3.5/bisect.* ${libdir}/python3.5/code.* ${libdir}/python3.5/codeop.* ${libdir}/python3.5/collections.* ${libdir}/python3.5/_collections_abc.* ${libdir}/python3.5/contextlib.* ${libdir}/python3.5/dis.* ${libdir}/python3.5/functools.* ${libdir}/python3.5/heapq.* ${libdir}/python3.5/inspect.* ${libdir}/python3.5/keyword.* ${libdir}/python3.5/opcode.* ${libdir}/python3.5/operator.* ${libdir}/python3.5/symbol.* ${libdir}/python3.5/repr.* ${libdir}/python3.5/token.* ${libdir}/python3.5/tokenize.* ${libdir}/python3.5/traceback.* ${libdir}/python3.5/weakref.* "

SUMMARY_${PN}-logging="Python logging support"
RDEPENDS_${PN}-logging="${PN}-core ${PN}-io ${PN}-lang ${PN}-pickle ${PN}-stringold"
FILES_${PN}-logging="${libdir}/python3.5/logging "

SUMMARY_${PN}-mailbox="Python mailbox format support"
RDEPENDS_${PN}-mailbox="${PN}-core ${PN}-mime"
FILES_${PN}-mailbox="${libdir}/python3.5/mailbox.* "

SUMMARY_${PN}-math="Python math support"
RDEPENDS_${PN}-math="${PN}-core ${PN}-crypt"
FILES_${PN}-math="${libdir}/python3.5/lib-dynload/cmath.*.so ${libdir}/python3.5/lib-dynload/math.*.so ${libdir}/python3.5/lib-dynload/_random.*.so ${libdir}/python3.5/random.* ${libdir}/python3.5/sets.* "

SUMMARY_${PN}-mime="Python MIME handling APIs"
RDEPENDS_${PN}-mime="${PN}-core ${PN}-io"
FILES_${PN}-mime="${libdir}/python3.5/mimetools.* ${libdir}/python3.5/uu.* ${libdir}/python3.5/quopri.* ${libdir}/python3.5/rfc822.* ${libdir}/python3.5/MimeWriter.* "

SUMMARY_${PN}-mmap="Python memory-mapped file support"
RDEPENDS_${PN}-mmap="${PN}-core ${PN}-io"
FILES_${PN}-mmap="${libdir}/python3.5/lib-dynload/mmap.*.so "

SUMMARY_${PN}-multiprocessing="Python multiprocessing support"
RDEPENDS_${PN}-multiprocessing="${PN}-core ${PN}-io ${PN}-lang ${PN}-pickle ${PN}-threading ${PN}-ctypes ${PN}-mmap"
FILES_${PN}-multiprocessing="${libdir}/python3.5/lib-dynload/_multiprocessing.*.so ${libdir}/python3.5/multiprocessing "

SUMMARY_${PN}-netclient="Python Internet Protocol clients"
RDEPENDS_${PN}-netclient="${PN}-core ${PN}-crypt ${PN}-datetime ${PN}-io ${PN}-lang ${PN}-logging ${PN}-mime"
FILES_${PN}-netclient="${libdir}/python3.5/*Cookie*.* ${libdir}/python3.5/base64.* ${libdir}/python3.5/cookielib.* ${libdir}/python3.5/ftplib.* ${libdir}/python3.5/gopherlib.* ${libdir}/python3.5/hmac.* ${libdir}/python3.5/httplib.* ${libdir}/python3.5/mimetypes.* ${libdir}/python3.5/nntplib.* ${libdir}/python3.5/poplib.* ${libdir}/python3.5/smtplib.* ${libdir}/python3.5/telnetlib.* ${libdir}/python3.5/urllib ${libdir}/python3.5/uuid.* ${libdir}/python3.5/rfc822.* ${libdir}/python3.5/mimetools.* "

SUMMARY_${PN}-netserver="Python Internet Protocol servers"
RDEPENDS_${PN}-netserver="${PN}-core ${PN}-netclient ${PN}-shell ${PN}-threading"
FILES_${PN}-netserver="${libdir}/python3.5/cgi.* ${libdir}/python3.5/*HTTPServer.* ${libdir}/python3.5/SocketServer.* "

SUMMARY_${PN}-numbers="Python number APIs"
RDEPENDS_${PN}-numbers="${PN}-core ${PN}-lang ${PN}-re"
FILES_${PN}-numbers="${libdir}/python3.5/decimal.* ${libdir}/python3.5/fractions.* ${libdir}/python3.5/numbers.* "

SUMMARY_${PN}-pickle="Python serialisation/persistence support"
RDEPENDS_${PN}-pickle="${PN}-core ${PN}-codecs ${PN}-io ${PN}-re"
FILES_${PN}-pickle="${libdir}/python3.5/pickle.* ${libdir}/python3.5/shelve.* ${libdir}/python3.5/lib-dynload/cPickle.*.so ${libdir}/python3.5/pickletools.* "

SUMMARY_${PN}-pkgutil="Python package extension utility support"
RDEPENDS_${PN}-pkgutil="${PN}-core"
FILES_${PN}-pkgutil="${libdir}/python3.5/pkgutil.* "

SUMMARY_${PN}-pprint="Python pretty-print support"
RDEPENDS_${PN}-pprint="${PN}-core ${PN}-io"
FILES_${PN}-pprint="${libdir}/python3.5/pprint.* "

SUMMARY_${PN}-profile="Python basic performance profiling support"
RDEPENDS_${PN}-profile="${PN}-core ${PN}-textutils"
FILES_${PN}-profile="${libdir}/python3.5/profile.* ${libdir}/python3.5/pstats.* ${libdir}/python3.5/cProfile.* ${libdir}/python3.5/lib-dynload/_lsprof.*.so "

SUMMARY_${PN}-pydoc="Python interactive help support"
RDEPENDS_${PN}-pydoc="${PN}-core ${PN}-lang ${PN}-stringold ${PN}-re"
FILES_${PN}-pydoc="${bindir}/pydoc ${libdir}/python3.5/pydoc.* ${libdir}/python3.5/pydoc_data "

SUMMARY_${PN}-re="Python Regular Expression APIs"
RDEPENDS_${PN}-re="${PN}-core"
FILES_${PN}-re="${libdir}/python3.5/re.* ${libdir}/python3.5/sre.* ${libdir}/python3.5/sre_compile.* ${libdir}/python3.5/sre_constants* ${libdir}/python3.5/sre_parse.* "

SUMMARY_${PN}-readline="Python readline support"
RDEPENDS_${PN}-readline="${PN}-core"
FILES_${PN}-readline="${libdir}/python3.5/lib-dynload/readline.*.so ${libdir}/python3.5/rlcompleter.* "

SUMMARY_${PN}-reprlib="Python alternate repr() implementation"
RDEPENDS_${PN}-reprlib="${PN}-core"
FILES_${PN}-reprlib="${libdir}/python3.5/reprlib.py "

SUMMARY_${PN}-resource="Python resource control interface"
RDEPENDS_${PN}-resource="${PN}-core"
FILES_${PN}-resource="${libdir}/python3.5/lib-dynload/resource.*.so "

SUMMARY_${PN}-selectors="Python High-level I/O multiplexing"
RDEPENDS_${PN}-selectors="${PN}-core"
FILES_${PN}-selectors="${libdir}/python3.5/selectors.* "

SUMMARY_${PN}-shell="Python shell-like functionality"
RDEPENDS_${PN}-shell="${PN}-core ${PN}-re ${PN}-compression"
FILES_${PN}-shell="${libdir}/python3.5/cmd.* ${libdir}/python3.5/commands.* ${libdir}/python3.5/dircache.* ${libdir}/python3.5/fnmatch.* ${libdir}/python3.5/glob.* ${libdir}/python3.5/popen2.* ${libdir}/python3.5/shlex.* ${libdir}/python3.5/shutil.* "

SUMMARY_${PN}-signal="Python set handlers for asynchronous events support"
RDEPENDS_${PN}-signal="${PN}-core ${PN}-enum"
FILES_${PN}-signal="${libdir}/python3.5/signal.* "

SUMMARY_${PN}-smtpd="Python Simple Mail Transport Daemon"
RDEPENDS_${PN}-smtpd="${PN}-core ${PN}-netserver ${PN}-email ${PN}-mime"
FILES_${PN}-smtpd="${bindir}/smtpd.* ${libdir}/python3.5/smtpd.* "

SUMMARY_${PN}-sqlite3="Python Sqlite3 database support"
RDEPENDS_${PN}-sqlite3="${PN}-core ${PN}-datetime ${PN}-lang ${PN}-crypt ${PN}-io ${PN}-threading"
FILES_${PN}-sqlite3="${libdir}/python3.5/lib-dynload/_sqlite3.*.so ${libdir}/python3.5/sqlite3/dbapi2.* ${libdir}/python3.5/sqlite3/__init__.* ${libdir}/python3.5/sqlite3/dump.* "

SUMMARY_${PN}-sqlite3-tests="Python Sqlite3 database support tests"
RDEPENDS_${PN}-sqlite3-tests="${PN}-core ${PN}-sqlite3"
FILES_${PN}-sqlite3-tests="${libdir}/python3.5/sqlite3/test "

SUMMARY_${PN}-stringold="Python string APIs [deprecated]"
RDEPENDS_${PN}-stringold="${PN}-core ${PN}-re"
FILES_${PN}-stringold="${libdir}/python3.5/lib-dynload/strop.*.so ${libdir}/python3.5/string.* ${libdir}/python3.5/stringold.* "

SUMMARY_${PN}-subprocess="Python subprocess support"
RDEPENDS_${PN}-subprocess="${PN}-core ${PN}-io ${PN}-re ${PN}-fcntl ${PN}-pickle ${PN}-threading ${PN}-signal ${PN}-selectors"
FILES_${PN}-subprocess="${libdir}/python3.5/subprocess.* ${libdir}/python3.5/lib-dynload/_posixsubprocess.*.so "

SUMMARY_${PN}-syslog="Python syslog interface"
RDEPENDS_${PN}-syslog="${PN}-core"
FILES_${PN}-syslog="${libdir}/python3.5/lib-dynload/syslog.*.so "

SUMMARY_${PN}-terminal="Python terminal controlling support"
RDEPENDS_${PN}-terminal="${PN}-core ${PN}-io"
FILES_${PN}-terminal="${libdir}/python3.5/pty.* ${libdir}/python3.5/tty.* "

SUMMARY_${PN}-tests="Python tests"
RDEPENDS_${PN}-tests="${PN}-core"
FILES_${PN}-tests="${libdir}/python3.5/test "

SUMMARY_${PN}-textutils="Python option parsing, text wrapping and CSV support"
RDEPENDS_${PN}-textutils="${PN}-core ${PN}-io ${PN}-re ${PN}-stringold"
FILES_${PN}-textutils="${libdir}/python3.5/lib-dynload/_csv.*.so ${libdir}/python3.5/csv.* ${libdir}/python3.5/optparse.* ${libdir}/python3.5/textwrap.* "

SUMMARY_${PN}-threading="Python threading & synchronization support"
RDEPENDS_${PN}-threading="${PN}-core ${PN}-lang"
FILES_${PN}-threading="${libdir}/python3.5/_threading_local.* ${libdir}/python3.5/dummy_thread.* ${libdir}/python3.5/dummy_threading.* ${libdir}/python3.5/mutex.* ${libdir}/python3.5/threading.* ${libdir}/python3.5/queue.* "

SUMMARY_${PN}-tkinter="Python Tcl/Tk bindings"
RDEPENDS_${PN}-tkinter="${PN}-core"
FILES_${PN}-tkinter="${libdir}/python3.5/lib-dynload/_tkinter.*.so ${libdir}/python3.5/lib-tk ${libdir}/python3.5/tkinter "

SUMMARY_${PN}-unittest="Python unit testing framework"
RDEPENDS_${PN}-unittest="${PN}-core ${PN}-stringold ${PN}-lang ${PN}-io ${PN}-difflib ${PN}-pprint ${PN}-shell"
FILES_${PN}-unittest="${libdir}/python3.5/unittest/ "

SUMMARY_${PN}-unixadmin="Python Unix administration support"
RDEPENDS_${PN}-unixadmin="${PN}-core"
FILES_${PN}-unixadmin="${libdir}/python3.5/lib-dynload/nis.*.so ${libdir}/python3.5/lib-dynload/grp.*.so ${libdir}/python3.5/lib-dynload/pwd.*.so ${libdir}/python3.5/getpass.* "

SUMMARY_${PN}-xml="Python basic XML support"
RDEPENDS_${PN}-xml="${PN}-core ${PN}-re"
FILES_${PN}-xml="${libdir}/python3.5/lib-dynload/_elementtree.*.so ${libdir}/python3.5/lib-dynload/pyexpat.*.so ${libdir}/python3.5/xml ${libdir}/python3.5/xmllib.* "

SUMMARY_${PN}-xmlrpc="Python XML-RPC support"
RDEPENDS_${PN}-xmlrpc="${PN}-core ${PN}-xml ${PN}-netserver ${PN}-lang"
FILES_${PN}-xmlrpc="${libdir}/python3.5/xmlrpclib.* ${libdir}/python3.5/SimpleXMLRPCServer.* ${libdir}/python3.5/DocXMLRPCServer.* ${libdir}/python3.5/xmlrpc "

SUMMARY_${PN}-modules="All Python modules"
RDEPENDS_${PN}-modules="${PN}-2to3 ${PN}-argparse ${PN}-asyncio ${PN}-audio ${PN}-codecs ${PN}-compile ${PN}-compression ${PN}-core ${PN}-crypt ${PN}-ctypes ${PN}-curses ${PN}-datetime ${PN}-db ${PN}-debugger ${PN}-difflib ${PN}-distutils ${PN}-doctest ${PN}-email ${PN}-enum ${PN}-fcntl ${PN}-gdbm ${PN}-html ${PN}-idle ${PN}-image ${PN}-importlib ${PN}-io ${PN}-json ${PN}-lang ${PN}-logging ${PN}-mailbox ${PN}-math ${PN}-mime ${PN}-mmap ${PN}-multiprocessing ${PN}-netclient ${PN}-netserver ${PN}-numbers ${PN}-pickle ${PN}-pkgutil ${PN}-pprint ${PN}-profile ${PN}-pydoc ${PN}-re ${PN}-readline ${PN}-reprlib ${PN}-resource ${PN}-selectors ${PN}-shell ${PN}-signal ${PN}-smtpd ${PN}-sqlite3 ${PN}-sqlite3-tests ${PN}-stringold ${PN}-subprocess ${PN}-syslog ${PN}-terminal ${PN}-tests ${PN}-textutils ${PN}-threading ${PN}-tkinter ${PN}-unittest ${PN}-unixadmin ${PN}-xml ${PN}-xmlrpc  "
ALLOW_EMPTY_${PN}-modules = "1"


