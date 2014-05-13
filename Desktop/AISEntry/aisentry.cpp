#include "aisentry.h"
#include <QtWidgets>

AISEntry::AISEntry(QWidget *parent)
    : QWidget(parent, Qt::FramelessWindowHint | Qt::WindowSystemMenuHint)
{
    /**
     * Set backgroud and window shape
     */
    setFixedSize(72, 72);

    QPixmap pixmap(":/images/icon");
    setWindowIcon(QIcon(pixmap));

    QPalette palette;
    palette.setBrush(backgroundRole(), QBrush(pixmap));
    setPalette(palette);

    QBitmap maskBitMap(":/images/icon_mask");
    setMask(maskBitMap);

    setMouseTracking(true);

    /**
     * set context menu
     */
    QAction *quitAction = new QAction(tr("E&xit"), this);
    connect(quitAction, SIGNAL(triggered()), qApp, SLOT(quit()));
    addAction(quitAction);

    setContextMenuPolicy(Qt::ActionsContextMenu);
    setWindowTitle(tr("AISEntry"));
}

AISEntry::~AISEntry()
{

}

void AISEntry::mousePressEvent(QMouseEvent *event)
{
    if (Qt::LeftButton == event->button())
    {
        dragPosition = event->globalPos() - frameGeometry().topLeft();
        event->accept();
    }
}

void AISEntry::mouseMoveEvent(QMouseEvent *event)
{
    if (event->buttons() & Qt::LeftButton)
    {
        move(event->globalPos() - dragPosition);
        setCursor(Qt::ClosedHandCursor);
        event->accept();
    }
    else
    {
        QPoint mousePosition;
        mousePosition = event->globalPos() - frameGeometry().topLeft();
        if (mousePosition.ry() < frameGeometry().height() / 2)
        {
            setCursor(Qt::PointingHandCursor);
        }
        else
        {
            setCursor(Qt::ArrowCursor);
        }
    }
}
