#
# BitBake Toaster Implementation
#
# Copyright (C) 2013        Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

# Django settings for Toaster project.

import os
from pathlib import Path
from toastermain.logs import LOGGING_SETTINGS

DEBUG = True

# Set to True to see the SQL queries in console
SQL_DEBUG = False
if os.environ.get("TOASTER_SQLDEBUG", None) is not None:
    SQL_DEBUG = True


ADMINS = (
    # ('Your Name', 'your_email@example.com'),
)

MANAGERS = ADMINS

TOASTER_SQLITE_DEFAULT_DIR = os.environ.get('TOASTER_DIR')

DATABASES = {
    'default': {
        # Add 'postgresql_psycopg2', 'mysql', 'sqlite3' or 'oracle'.
        'ENGINE': 'django.db.backends.sqlite3',
        # DB name or full path to database file if using sqlite3.
        'NAME': "%s/toaster.sqlite" % TOASTER_SQLITE_DEFAULT_DIR,
        'USER': '',
        'PASSWORD': '',
        #'HOST': '127.0.0.1', # e.g. mysql server
        #'PORT': '3306', # e.g. mysql port
    }
}

# New in Django 3.2
DEFAULT_AUTO_FIELD = 'django.db.models.BigAutoField'

# Needed when Using sqlite especially to add a longer timeout for waiting
# for the database lock to be  released
# https://docs.djangoproject.com/en/1.6/ref/databases/#database-is-locked-errors
if 'sqlite' in DATABASES['default']['ENGINE']:
    DATABASES['default']['OPTIONS'] = { 'timeout': 20 }

# Update as of django 1.8.16 release, the '*' is needed to allow us to connect while running
# on hosts without explicitly setting the fqdn for the toaster server.
# See https://docs.djangoproject.com/en/dev/ref/settings/ for info on ALLOWED_HOSTS
# Previously this setting was not enforced if DEBUG was set but it is now.
# The previous behavior was such that ALLOWED_HOSTS defaulted to ['localhost','127.0.0.1','::1']
# and if you bound to 0.0.0.0:<port #> then accessing toaster as localhost or fqdn would both work.
# To have that same behavior, with a fqdn explicitly enabled you would set
# ALLOWED_HOSTS= ['localhost','127.0.0.1','::1','myserver.mycompany.com'] for
# Django >= 1.8.16. By default, we are not enforcing this restriction in
# DEBUG mode.
if DEBUG is True:
    # this will allow connection via localhost,hostname, or fqdn
    ALLOWED_HOSTS = ['*']

# Local time zone for this installation. Choices can be found here:
# http://en.wikipedia.org/wiki/List_of_tz_zones_by_name
# although not all choices may be available on all operating systems.
# In a Windows environment this must be set to your system time zone.

# Always use local computer's time zone, find
import hashlib
if 'TZ' in os.environ:
    TIME_ZONE = os.environ['TZ']
else:
    # need to read the /etc/localtime file which is the libc standard
    # and do a reverse-mapping to /usr/share/zoneinfo/;
    # since the timezone may match any number of identical timezone definitions,

    zonefilelist = {}
    ZONEINFOPATH = '/usr/share/zoneinfo/'
    for dirpath, dirnames, filenames in os.walk(ZONEINFOPATH):
        for fn in filenames:
            filepath = os.path.join(dirpath, fn)
            zonename = filepath.lstrip(ZONEINFOPATH).strip()
            try:
                import pytz
                from pytz.exceptions import UnknownTimeZoneError
                try:
                    if pytz.timezone(zonename) is not None:
                        zonefilelist[hashlib.md5(open(filepath, 'rb').read()).hexdigest()] = zonename
                except UnknownTimeZoneError as ValueError:
                    # we expect timezone failures here, just move over
                    pass
            except ImportError:
                zonefilelist[hashlib.md5(open(filepath, 'rb').read()).hexdigest()] = zonename

    TIME_ZONE = zonefilelist[hashlib.md5(open('/etc/localtime', 'rb').read()).hexdigest()]

# Language code for this installation. All choices can be found here:
# http://www.i18nguy.com/unicode/language-identifiers.html
LANGUAGE_CODE = 'en-us'

SITE_ID = 1

# If you set this to False, Django will make some optimizations so as not
# to load the internationalization machinery.
USE_I18N = True

# If you set this to False, Django will not format dates, numbers and
# calendars according to the current locale.
USE_L10N = True

# If you set this to False, Django will not use timezone-aware datetimes.
USE_TZ = True

# Absolute filesystem path to the directory that will hold user-uploaded files.
# Example: "/var/www/example.com/media/"
MEDIA_ROOT = ''

# URL that handles the media served from MEDIA_ROOT. Make sure to use a
# trailing slash.
# Examples: "http://example.com/media/", "http://media.example.com/"
MEDIA_URL = ''

# Absolute path to the directory static files should be collected to.
# Don't put anything in this directory yourself; store your static files
# in apps' "static/" subdirectories and in STATICFILES_DIRS.
# Example: "/var/www/example.com/static/"
STATIC_ROOT = ''

# URL prefix for static files.
# Example: "http://example.com/static/", "http://static.example.com/"
STATIC_URL = '/static/'

# Additional locations of static files
STATICFILES_DIRS = (
    # Put strings here, like "/home/html/static" or "C:/www/django/static".
    # Always use forward slashes, even on Windows.
    # Don't forget to use absolute paths, not relative paths.
)

# List of finder classes that know how to find static files in
# various locations.
STATICFILES_FINDERS = (
    'django.contrib.staticfiles.finders.FileSystemFinder',
    'django.contrib.staticfiles.finders.AppDirectoriesFinder',
#    'django.contrib.staticfiles.finders.DefaultStorageFinder',
)

# Make this unique, and don't share it with anybody.
SECRET_KEY = 'NOT_SUITABLE_FOR_HOSTED_DEPLOYMENT'

class InvalidString(str):
    def __mod__(self, other):
        from django.template.base import TemplateSyntaxError
        raise TemplateSyntaxError(
            "Undefined variable or unknown value for: \"%s\"" % other)

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [
            # Put strings here, like "/home/html/django_templates" or "C:/www/django/templates".
            # Always use forward slashes, even on Windows.
            # Don't forget to use absolute paths, not relative paths.
        ],
        'OPTIONS': {
            'context_processors': [
                # Insert your TEMPLATE_CONTEXT_PROCESSORS here or use this
                # list if you haven't customized them:
                'django.contrib.auth.context_processors.auth',
                'django.template.context_processors.debug',
                'django.template.context_processors.i18n',
                'django.template.context_processors.media',
                'django.template.context_processors.static',
                'django.template.context_processors.tz',
                'django.contrib.messages.context_processors.messages',
                # Custom
                'django.template.context_processors.request',
                'toastergui.views.managedcontextprocessor',

            ],
            'loaders': [
                # List of callables that know how to import templates from various sources.
                'django.template.loaders.filesystem.Loader',
                'django.template.loaders.app_directories.Loader',
                #'django.template.loaders.eggs.Loader',
            ],
            # https://docs.djangoproject.com/en/4.2/ref/templates/api/#how-invalid-variables-are-handled
            # Generally, string_if_invalid should only be enabled in order to debug
            # a specific template problem, then cleared once debugging is complete.
            # If you assign a value other than '' to string_if_invalid,
            # you will experience rendering problems with these templates and sites.
            #  'string_if_invalid': InvalidString("%s"),
            'string_if_invalid': "",
            'debug': DEBUG,
        },
    },
]

MIDDLEWARE = [
    'django.middleware.common.CommonMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
]

CACHES = {
    #        'default': {
    #            'BACKEND': 'django.core.cache.backends.memcached.MemcachedCache',
    #            'LOCATION': '127.0.0.1:11211',
    #        },
           'default': {
               'BACKEND': 'django.core.cache.backends.filebased.FileBasedCache',
               'LOCATION': '/tmp/toaster_cache_%d' % os.getuid(),
               'TIMEOUT': 1,
            }
          }


from os.path import dirname as DN
SITE_ROOT=DN(DN(os.path.abspath(__file__)))

import subprocess
TOASTER_BRANCH = subprocess.Popen('git branch | grep "^* " | tr -d "* "', cwd = SITE_ROOT, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()[0]
TOASTER_REVISION = subprocess.Popen('git rev-parse HEAD ', cwd = SITE_ROOT, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()[0]

ROOT_URLCONF = 'toastermain.urls'

# Python dotted path to the WSGI application used by Django's runserver.
WSGI_APPLICATION = 'toastermain.wsgi.application'


INSTALLED_APPS = (
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.messages',
    'django.contrib.sessions',
    'django.contrib.admin',
    'django.contrib.staticfiles',

    # Uncomment the next line to enable admin documentation:
    # 'django.contrib.admindocs',
    'django.contrib.humanize',
    'bldcollector',
    'toastermain',

    # 3rd-lib
    "log_viewer",
)


INTERNAL_IPS = ['127.0.0.1', '192.168.2.28']

# Load django-fresh is TOASTER_DEVEL is set, and the module is available
FRESH_ENABLED = False
if os.environ.get('TOASTER_DEVEL', None) is not None:
    try:
        import fresh
        MIDDLEWARE = ["fresh.middleware.FreshMiddleware",] + MIDDLEWARE
        INSTALLED_APPS = INSTALLED_APPS + ('fresh',)
        FRESH_ENABLED = True
    except:
        pass

DEBUG_PANEL_ENABLED = False
if os.environ.get('TOASTER_DEVEL', None) is not None:
    try:
        import debug_toolbar, debug_panel
        MIDDLEWARE = ['debug_panel.middleware.DebugPanelMiddleware',] + MIDDLEWARE
        #MIDDLEWARE = MIDDLEWARE + ['debug_toolbar.middleware.DebugToolbarMiddleware',]
        INSTALLED_APPS = INSTALLED_APPS + ('debug_toolbar','debug_panel',)
        DEBUG_PANEL_ENABLED = True

        # this cache backend will be used by django-debug-panel
        CACHES['debug-panel'] = {
                'BACKEND': 'django.core.cache.backends.filebased.FileBasedCache',
                'LOCATION': '/var/tmp/debug-panel-cache',
                'TIMEOUT': 300,
                'OPTIONS': {
                    'MAX_ENTRIES': 200
                }
        }

    except:
        pass


SOUTH_TESTS_MIGRATE = False


# We automatically detect and install applications here if
# they have a 'models.py' or 'views.py' file
import os
currentdir = os.path.dirname(__file__)
for t in os.walk(os.path.dirname(currentdir)):
    modulename = os.path.basename(t[0])
    #if we have a virtualenv skip it to avoid incorrect imports
    if 'VIRTUAL_ENV' in os.environ and os.environ['VIRTUAL_ENV'] in t[0]:
        continue

    if ("views.py" in t[2] or "models.py" in t[2]) and not modulename in INSTALLED_APPS:
        INSTALLED_APPS = INSTALLED_APPS + (modulename,)

# A sample logging configuration. The only tangible logging
# performed by this configuration is to send an email to
# the site admins on every HTTP 500 error when DEBUG=False.
# See http://docs.djangoproject.com/en/dev/topics/logging for
# more details on how to customize your logging configuration.
LOGGING = LOGGING_SETTINGS

# Build paths inside the project like this: BASE_DIR / 'subdir'.
BASE_DIR = Path(__file__).resolve(strict=True).parent.parent

# LOG VIEWER
# https://pypi.org/project/django-log-viewer/
LOG_VIEWER_FILES_PATTERN = '*.log*'
LOG_VIEWER_FILES_DIR = os.path.join(BASE_DIR, 'logs')
LOG_VIEWER_PAGE_LENGTH = 25      # total log lines per-page
LOG_VIEWER_MAX_READ_LINES = 100000  # total log lines will be read
LOG_VIEWER_PATTERNS = ['INFO', 'DEBUG', 'WARNING', 'ERROR', 'CRITICAL']

# Optionally you can set the next variables in order to customize the admin:
LOG_VIEWER_FILE_LIST_TITLE = "Logs list"


if DEBUG and SQL_DEBUG:
    LOGGING['loggers']['django.db.backends'] = {
            'level': 'DEBUG',
            'handlers': ['console'],
        }


# If we're using sqlite, we need to tweak the performance a bit
from django.db.backends.signals import connection_created
def activate_synchronous_off(sender, connection, **kwargs):
    if connection.vendor == 'sqlite':
        cursor = connection.cursor()
        cursor.execute('PRAGMA synchronous = 0;')
connection_created.connect(activate_synchronous_off)
#

