#-------------------------------------------------
#
# Project created by QtCreator 2014-05-12T15:20:11
#
#-------------------------------------------------

QT       += core gui network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = AISEntry
TEMPLATE = app


SOURCES += main.cpp\
        aisentry.cpp \
    peermanager.cpp \
    connection.cpp \
    server.cpp \
    transceiver.cpp

HEADERS  += aisentry.h \
    peermanager.h \
    connection.h \
    server.h \
    transceiver.h

RESOURCES += \
    res.qrc

TRANSLATIONS += lang_CN.ts
