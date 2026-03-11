#! /usr/bin/env python3
#
# Copyright (C) 2023 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
from datetime import datetime
from . import User

from sqlalchemy.ext.asyncio import create_async_engine
from sqlalchemy.pool import NullPool
from sqlalchemy import (
    MetaData,
    Column,
    Table,
    Text,
    Integer,
    UniqueConstraint,
    DateTime,
    Index,
    select,
    insert,
    exists,
    literal,
    and_,
    delete,
    update,
    func,
    inspect,
)
import sqlalchemy.engine
from sqlalchemy.orm import declarative_base
from sqlalchemy.exc import IntegrityError
from sqlalchemy.dialects.postgresql import insert as postgres_insert

Base = declarative_base()


class UnihashesV3(Base):
    __tablename__ = "unihashes_v3"
    id = Column(Integer, primary_key=True, autoincrement=True)
    method = Column(Text, nullable=False)
    taskhash = Column(Text, nullable=False)
    unihash = Column(Text, nullable=False)
    gc_mark = Column(Text, nullable=False)

    __table_args__ = (
        UniqueConstraint("method", "taskhash"),
        Index("taskhash_lookup_v4", "method", "taskhash"),
        Index("unihash_lookup_v1", "unihash"),
    )


class OuthashesV2(Base):
    __tablename__ = "outhashes_v2"
    id = Column(Integer, primary_key=True, autoincrement=True)
    method = Column(Text, nullable=False)
    taskhash = Column(Text, nullable=False)
    outhash = Column(Text, nullable=False)
    created = Column(DateTime)
    owner = Column(Text)
    PN = Column(Text)
    PV = Column(Text)
    PR = Column(Text)
    task = Column(Text)
    outhash_siginfo = Column(Text)

    __table_args__ = (
        UniqueConstraint("method", "taskhash", "outhash"),
        Index("outhash_lookup_v3", "method", "outhash"),
    )


class Users(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True, autoincrement=True)
    username = Column(Text, nullable=False)
    token = Column(Text, nullable=False)
    permissions = Column(Text)

    __table_args__ = (UniqueConstraint("username"),)


class Config(Base):
    __tablename__ = "config"
    id = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(Text, nullable=False)
    value = Column(Text)
    __table_args__ = (
        UniqueConstraint("name"),
        Index("config_lookup", "name"),
    )


#
# Old table versions
#
DeprecatedBase = declarative_base()


class UnihashesV2(DeprecatedBase):
    __tablename__ = "unihashes_v2"
    id = Column(Integer, primary_key=True, autoincrement=True)
    method = Column(Text, nullable=False)
    taskhash = Column(Text, nullable=False)
    unihash = Column(Text, nullable=False)

    __table_args__ = (
        UniqueConstraint("method", "taskhash"),
        Index("taskhash_lookup_v3", "method", "taskhash"),
    )


class DatabaseEngine(object):
    def __init__(self, url, username=None, password=None):
        self.logger = logging.getLogger("hashserv.sqlalchemy")
        self.url = sqlalchemy.engine.make_url(url)

        if username is not None:
            self.url = self.url.set(username=username)

        if password is not None:
            self.url = self.url.set(password=password)

    async def create(self):
        def check_table_exists(conn, name):
            return inspect(conn).has_table(name)

        self.logger.info("Using database %s", self.url)
        if self.url.drivername == 'postgresql+psycopg':
            # Psygopg 3 (psygopg) driver can handle async connection pooling
            self.engine = create_async_engine(self.url, max_overflow=-1)
        else:
            self.engine = create_async_engine(self.url, poolclass=NullPool)

        async with self.engine.begin() as conn:
            # Create tables
            self.logger.info("Creating tables...")
            await conn.run_sync(Base.metadata.create_all)

            if await conn.run_sync(check_table_exists, UnihashesV2.__tablename__):
                self.logger.info("Upgrading Unihashes V2 -> V3...")
                statement = insert(UnihashesV3).from_select(
                    ["id", "method", "unihash", "taskhash", "gc_mark"],
                    select(
                        UnihashesV2.id,
                        UnihashesV2.method,
                        UnihashesV2.unihash,
                        UnihashesV2.taskhash,
                        literal("").label("gc_mark"),
                    ),
                )
                self.logger.debug("%s", statement)
                await conn.execute(statement)

                await conn.run_sync(Base.metadata.drop_all, [UnihashesV2.__table__])
                self.logger.info("Upgrade complete")

    def connect(self, logger):
        return Database(self.engine, logger)


def map_row(row):
    if row is None:
        return None
    return dict(**row._mapping)


def map_user(row):
    if row is None:
        return None
    return User(
        username=row.username,
        permissions=set(row.permissions.split()),
    )


def _make_condition_statement(table, condition):
    where = {}
    for c in table.__table__.columns:
        if c.key in condition and condition[c.key] is not None:
            where[c] = condition[c.key]

    return [(k == v) for k, v in where.items()]


class Database(object):
    def __init__(self, engine, logger):
        self.engine = engine
        self.db = None
        self.logger = logger

    async def __aenter__(self):
        self.db = await self.engine.connect()
        return self

    async def __aexit__(self, exc_type, exc_value, traceback):
        await self.close()

    async def close(self):
        await self.db.close()
        self.db = None

    async def _execute(self, statement):
        self.logger.debug("%s", statement)
        return await self.db.execute(statement)

    async def _set_config(self, name, value):
        while True:
            result = await self._execute(
                update(Config).where(Config.name == name).values(value=value)
            )

            if result.rowcount == 0:
                self.logger.debug("Config '%s' not found. Adding it", name)
                try:
                    await self._execute(insert(Config).values(name=name, value=value))
                except IntegrityError:
                    # Race. Try again
                    continue

            break

    def _get_config_subquery(self, name, default=None):
        if default is not None:
            return func.coalesce(
                select(Config.value).where(Config.name == name).scalar_subquery(),
                default,
            )
        return select(Config.value).where(Config.name == name).scalar_subquery()

    async def _get_config(self, name):
        result = await self._execute(select(Config.value).where(Config.name == name))
        row = result.first()
        if row is None:
            return None
        return row.value

    async def get_unihash_by_taskhash_full(self, method, taskhash):
        async with self.db.begin():
            result = await self._execute(
                select(
                    OuthashesV2,
                    UnihashesV3.unihash.label("unihash"),
                )
                .join(
                    UnihashesV3,
                    and_(
                        UnihashesV3.method == OuthashesV2.method,
                        UnihashesV3.taskhash == OuthashesV2.taskhash,
                    ),
                )
                .where(
                    OuthashesV2.method == method,
                    OuthashesV2.taskhash == taskhash,
                )
                .order_by(
                    OuthashesV2.created.asc(),
                )
                .limit(1)
            )
            return map_row(result.first())

    async def get_unihash_by_outhash(self, method, outhash):
        async with self.db.begin():
            result = await self._execute(
                select(OuthashesV2, UnihashesV3.unihash.label("unihash"))
                .join(
                    UnihashesV3,
                    and_(
                        UnihashesV3.method == OuthashesV2.method,
                        UnihashesV3.taskhash == OuthashesV2.taskhash,
                    ),
                )
                .where(
                    OuthashesV2.method == method,
                    OuthashesV2.outhash == outhash,
                )
                .order_by(
                    OuthashesV2.created.asc(),
                )
                .limit(1)
            )
            return map_row(result.first())

    async def unihash_exists(self, unihash):
        async with self.db.begin():
            result = await self._execute(
                select(UnihashesV3).where(UnihashesV3.unihash == unihash).limit(1)
            )

            return result.first() is not None

    async def get_outhash(self, method, outhash):
        async with self.db.begin():
            result = await self._execute(
                select(OuthashesV2)
                .where(
                    OuthashesV2.method == method,
                    OuthashesV2.outhash == outhash,
                )
                .order_by(
                    OuthashesV2.created.asc(),
                )
                .limit(1)
            )
            return map_row(result.first())

    async def get_equivalent_for_outhash(self, method, outhash, taskhash):
        async with self.db.begin():
            result = await self._execute(
                select(
                    OuthashesV2.taskhash.label("taskhash"),
                    UnihashesV3.unihash.label("unihash"),
                )
                .join(
                    UnihashesV3,
                    and_(
                        UnihashesV3.method == OuthashesV2.method,
                        UnihashesV3.taskhash == OuthashesV2.taskhash,
                    ),
                )
                .where(
                    OuthashesV2.method == method,
                    OuthashesV2.outhash == outhash,
                    OuthashesV2.taskhash != taskhash,
                )
                .order_by(
                    OuthashesV2.created.asc(),
                )
                .limit(1)
            )
            return map_row(result.first())

    async def get_equivalent(self, method, taskhash):
        async with self.db.begin():
            result = await self._execute(
                select(
                    UnihashesV3.unihash,
                    UnihashesV3.method,
                    UnihashesV3.taskhash,
                ).where(
                    UnihashesV3.method == method,
                    UnihashesV3.taskhash == taskhash,
                )
            )
            return map_row(result.first())

    async def remove(self, condition):
        async def do_remove(table):
            where = _make_condition_statement(table, condition)
            if where:
                async with self.db.begin():
                    result = await self._execute(delete(table).where(*where))
                return result.rowcount

            return 0

        count = 0
        count += await do_remove(UnihashesV3)
        count += await do_remove(OuthashesV2)

        return count

    async def get_current_gc_mark(self):
        async with self.db.begin():
            return await self._get_config("gc-mark")

    async def gc_status(self):
        async with self.db.begin():
            gc_mark_subquery = self._get_config_subquery("gc-mark", "")

            result = await self._execute(
                select(func.count())
                .select_from(UnihashesV3)
                .where(UnihashesV3.gc_mark == gc_mark_subquery)
            )
            keep_rows = result.scalar()

            result = await self._execute(
                select(func.count())
                .select_from(UnihashesV3)
                .where(UnihashesV3.gc_mark != gc_mark_subquery)
            )
            remove_rows = result.scalar()

            return (keep_rows, remove_rows, await self._get_config("gc-mark"))

    async def gc_mark(self, mark, condition):
        async with self.db.begin():
            await self._set_config("gc-mark", mark)

            where = _make_condition_statement(UnihashesV3, condition)
            if not where:
                return 0

            result = await self._execute(
                update(UnihashesV3)
                .values(gc_mark=self._get_config_subquery("gc-mark", ""))
                .where(*where)
            )
            return result.rowcount

    async def gc_sweep(self):
        async with self.db.begin():
            result = await self._execute(
                delete(UnihashesV3).where(
                    # A sneaky conditional that provides some errant use
                    # protection: If the config mark is NULL, this will not
                    # match any rows because No default is specified in the
                    # select statement
                    UnihashesV3.gc_mark
                    != self._get_config_subquery("gc-mark")
                )
            )
            await self._set_config("gc-mark", None)

            return result.rowcount

    async def clean_unused(self, oldest):
        async with self.db.begin():
            result = await self._execute(
                delete(OuthashesV2).where(
                    OuthashesV2.created < oldest,
                    ~(
                        select(UnihashesV3.id)
                        .where(
                            UnihashesV3.method == OuthashesV2.method,
                            UnihashesV3.taskhash == OuthashesV2.taskhash,
                        )
                        .limit(1)
                        .exists()
                    ),
                )
            )
            return result.rowcount

    async def insert_unihash(self, method, taskhash, unihash):
        # Postgres specific ignore on insert duplicate
        if self.engine.name == "postgresql":
            statement = (
                postgres_insert(UnihashesV3)
                .values(
                    method=method,
                    taskhash=taskhash,
                    unihash=unihash,
                    gc_mark=self._get_config_subquery("gc-mark", ""),
                )
                .on_conflict_do_nothing(index_elements=("method", "taskhash"))
            )
        else:
            statement = insert(UnihashesV3).values(
                method=method,
                taskhash=taskhash,
                unihash=unihash,
                gc_mark=self._get_config_subquery("gc-mark", ""),
            )

        try:
            async with self.db.begin():
                result = await self._execute(statement)
                return result.rowcount != 0
        except IntegrityError:
            self.logger.debug(
                "%s, %s, %s already in unihash database", method, taskhash, unihash
            )
            return False

    async def insert_outhash(self, data):
        outhash_columns = set(c.key for c in OuthashesV2.__table__.columns)

        data = {k: v for k, v in data.items() if k in outhash_columns}

        if "created" in data and not isinstance(data["created"], datetime):
            data["created"] = datetime.fromisoformat(data["created"])

        # Postgres specific ignore on insert duplicate
        if self.engine.name == "postgresql":
            statement = (
                postgres_insert(OuthashesV2)
                .values(**data)
                .on_conflict_do_nothing(
                    index_elements=("method", "taskhash", "outhash")
                )
            )
        else:
            statement = insert(OuthashesV2).values(**data)

        try:
            async with self.db.begin():
                result = await self._execute(statement)
                return result.rowcount != 0
        except IntegrityError:
            self.logger.debug(
                "%s, %s already in outhash database", data["method"], data["outhash"]
            )
            return False

    async def _get_user(self, username):
        async with self.db.begin():
            result = await self._execute(
                select(
                    Users.username,
                    Users.permissions,
                    Users.token,
                ).where(
                    Users.username == username,
                )
            )
            return result.first()

    async def lookup_user_token(self, username):
        row = await self._get_user(username)
        if not row:
            return None, None
        return map_user(row), row.token

    async def lookup_user(self, username):
        return map_user(await self._get_user(username))

    async def set_user_token(self, username, token):
        async with self.db.begin():
            result = await self._execute(
                update(Users)
                .where(
                    Users.username == username,
                )
                .values(
                    token=token,
                )
            )
            return result.rowcount != 0

    async def set_user_perms(self, username, permissions):
        async with self.db.begin():
            result = await self._execute(
                update(Users)
                .where(Users.username == username)
                .values(permissions=" ".join(permissions))
            )
            return result.rowcount != 0

    async def get_all_users(self):
        async with self.db.begin():
            result = await self._execute(
                select(
                    Users.username,
                    Users.permissions,
                )
            )
            return [map_user(row) for row in result]

    async def new_user(self, username, permissions, token):
        try:
            async with self.db.begin():
                await self._execute(
                    insert(Users).values(
                        username=username,
                        permissions=" ".join(permissions),
                        token=token,
                    )
                )
            return True
        except IntegrityError as e:
            self.logger.debug("Cannot create new user %s: %s", username, e)
            return False

    async def delete_user(self, username):
        async with self.db.begin():
            result = await self._execute(
                delete(Users).where(Users.username == username)
            )
            return result.rowcount != 0

    async def get_usage(self):
        usage = {}
        async with self.db.begin() as session:
            for name, table in Base.metadata.tables.items():
                result = await self._execute(
                    statement=select(func.count()).select_from(table)
                )
                usage[name] = {
                    "rows": result.scalar(),
                }

        return usage

    async def get_query_columns(self):
        columns = set()
        for table in (UnihashesV3, OuthashesV2):
            for c in table.__table__.columns:
                if not isinstance(c.type, Text):
                    continue
                columns.add(c.key)

        return list(columns)
