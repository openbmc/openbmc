#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.core.management.base import BaseCommand
from django.test.client import Client
import os, sys, re
import requests
from django.conf import settings

# pylint: disable=E1103
# Instance of 'WSGIRequest' has no 'status_code' member
# (but some types could not be inferred) (maybe-no-member)


class Command(BaseCommand):
    help    = "Test the response time for all toaster urls"

    def handle(self, *args, **options):
        root_urlconf = __import__(settings.ROOT_URLCONF)
        patterns = root_urlconf.urls.urlpatterns
        global full_url
        for pat in patterns:
            if pat.__class__.__name__ == 'RegexURLResolver':
                url_root_res = str(pat).split('^')[1].replace('>', '')
            if 'gui' in url_root_res:
                for url_patt in pat.url_patterns:
                    full_url = self.get_full_url(url_patt, url_root_res)
                    info = self.url_info(full_url)
                    status_code = info[0]
                    load_time = info[1]
                    print('Trying \'' + full_url + '\', ' + str(status_code) + ', ' + str(load_time))

    def get_full_url(self, url_patt, url_root_res):
        full_url = str(url_patt).split('^')[1].replace('$>', '').replace('(?P<file_path>(?:/[', '/bin/busybox').replace('.*', '')
        full_url = str(url_root_res + full_url)
        full_url = re.sub('\(\?P<.*?>\\\d\+\)', '1', full_url)
        full_url = 'http://localhost:8000/' + full_url
        return full_url

    def url_info(self, full_url):
        client = Client()
        info = []
        try:
            resp = client.get(full_url, follow = True)
        except Exception as e_status_code:
            self.error('Url: %s, error: %s' % (full_url, e_status_code))
            resp = type('object', (), {'status_code':0, 'content': str(e_status_code)})
        status_code = resp.status_code
        info.append(status_code)
        try:
            req = requests.get(full_url)
        except Exception as e_load_time:
            self.error('Url: %s, error: %s' % (full_url, e_load_time))
        load_time = req.elapsed
        info.append(load_time)
        return info

    def error(self, *args):
        for arg in args:
            print(arg, end=' ', file=sys.stderr)
        print(file=sys.stderr)
