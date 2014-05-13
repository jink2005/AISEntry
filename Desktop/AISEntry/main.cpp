#include "aisentry.h"
#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    AISEntry w;
    w.show();

    return a.exec();
}
