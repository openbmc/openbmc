# Copyright (C) 2018 Garmin Ltd.
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

from http.server import BaseHTTPRequestHandler, HTTPServer
import contextlib
import urllib.parse
import sqlite3
import json
import traceback
import logging
from datetime import datetime

logger = logging.getLogger('hashserv')

class HashEquivalenceServer(BaseHTTPRequestHandler):
    def log_message(self, f, *args):
        logger.debug(f, *args)

    def do_GET(self):
        try:
            p = urllib.parse.urlparse(self.path)

            if p.path != self.prefix + '/v1/equivalent':
                self.send_error(404)
                return

            query = urllib.parse.parse_qs(p.query, strict_parsing=True)
            method = query['method'][0]
            taskhash = query['taskhash'][0]

            d = None
            with contextlib.closing(self.db.cursor()) as cursor:
                cursor.execute('SELECT taskhash, method, unihash FROM tasks_v1 WHERE method=:method AND taskhash=:taskhash ORDER BY created ASC LIMIT 1',
                        {'method': method, 'taskhash': taskhash})

                row = cursor.fetchone()

                if row is not None:
                    logger.debug('Found equivalent task %s', row['taskhash'])
                    d = {k: row[k] for k in ('taskhash', 'method', 'unihash')}

            self.send_response(200)
            self.send_header('Content-Type', 'application/json; charset=utf-8')
            self.end_headers()
            self.wfile.write(json.dumps(d).encode('utf-8'))
        except:
            logger.exception('Error in GET')
            self.send_error(400, explain=traceback.format_exc())
            return

    def do_POST(self):
        try:
            p = urllib.parse.urlparse(self.path)

            if p.path != self.prefix + '/v1/equivalent':
                self.send_error(404)
                return

            length = int(self.headers['content-length'])
            data = json.loads(self.rfile.read(length).decode('utf-8'))

            with contextlib.closing(self.db.cursor()) as cursor:
                cursor.execute('''
                    SELECT taskhash, method, unihash FROM tasks_v1 WHERE method=:method AND outhash=:outhash
                    ORDER BY CASE WHEN taskhash=:taskhash THEN 1 ELSE 2 END,
                        created ASC
                    LIMIT 1
                    ''', {k: data[k] for k in ('method', 'outhash', 'taskhash')})

                row = cursor.fetchone()

                if row is None or row['taskhash'] != data['taskhash']:
                    unihash = data['unihash']
                    if row is not None:
                        unihash = row['unihash']

                    insert_data = {
                            'method': data['method'],
                            'outhash': data['outhash'],
                            'taskhash': data['taskhash'],
                            'unihash': unihash,
                            'created': datetime.now()
                            }

                    for k in ('owner', 'PN', 'PV', 'PR', 'task', 'outhash_siginfo'):
                        if k in data:
                            insert_data[k] = data[k]

                    cursor.execute('''INSERT INTO tasks_v1 (%s) VALUES (%s)''' % (
                            ', '.join(sorted(insert_data.keys())),
                            ', '.join(':' + k for k in sorted(insert_data.keys()))),
                        insert_data)

                    logger.info('Adding taskhash %s with unihash %s', data['taskhash'], unihash)
                    cursor.execute('SELECT taskhash, method, unihash FROM tasks_v1 WHERE id=:id', {'id': cursor.lastrowid})
                    row = cursor.fetchone()

                    self.db.commit()

                d = {k: row[k] for k in ('taskhash', 'method', 'unihash')}

                self.send_response(200)
                self.send_header('Content-Type', 'application/json; charset=utf-8')
                self.end_headers()
                self.wfile.write(json.dumps(d).encode('utf-8'))
        except:
            logger.exception('Error in POST')
            self.send_error(400, explain=traceback.format_exc())
            return

def create_server(addr, db, prefix=''):
    class Handler(HashEquivalenceServer):
        pass

    Handler.prefix = prefix
    Handler.db = db
    db.row_factory = sqlite3.Row

    with contextlib.closing(db.cursor()) as cursor:
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS tasks_v1 (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                method TEXT NOT NULL,
                outhash TEXT NOT NULL,
                taskhash TEXT NOT NULL,
                unihash TEXT NOT NULL,
                created DATETIME,

                -- Optional fields
                owner TEXT,
                PN TEXT,
                PV TEXT,
                PR TEXT,
                task TEXT,
                outhash_siginfo TEXT
                )
            ''')

    logger.info('Starting server on %s', addr)
    return HTTPServer(addr, Handler)
