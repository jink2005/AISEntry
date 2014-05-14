#include "aisentry.h"
#include <QtWidgets>

AISEntry::AISEntry(QWidget *parent)
    : QWidget(parent, Qt::FramelessWindowHint | Qt::WindowStaysOnTopHint | Qt::WindowSystemMenuHint)
{
    setFocusPolicy(Qt::ClickFocus);
    /**
     * Set backgroud and window shape
     */
    setWindowShape();

    QPixmap pixmap(":/images/icon");
    setWindowIcon(QIcon(pixmap));

    setMouseTracking(true);

    connect(qApp, SIGNAL(focusChanged(QWidget*,QWidget*)), this, SLOT(focusChanged(QWidget*,QWidget*)));

    /**
     * set context menu
     */

    QAction *speakAction = new QAction(tr("&Speak"), this);
    connect(speakAction, SIGNAL(triggered()), this, SLOT(speak()));
    addAction(speakAction);
    connect(this, SIGNAL(buttonClickedEvent(QMouseEvent*)), SLOT(speak()));

    QAction *aboutAction = new QAction(tr("&About"), this);
    connect(aboutAction, SIGNAL(triggered()), this, SLOT(about()));
    addAction(aboutAction);

    QAction *quitAction = new QAction(tr("E&xit"), this);
    connect(quitAction, SIGNAL(triggered()), qApp, SLOT(quit()));
    addAction(quitAction);

    setContextMenuPolicy(Qt::ActionsContextMenu);
    setWindowTitle(tr("AISEntry"));
}

AISEntry::~AISEntry()
{

}

void AISEntry::setWindowShape()
{
    QPixmap pixmap(":/images/icon");
    setFixedSize(pixmap.size());
    QPalette palette;
    palette.setBrush(backgroundRole(), QBrush(pixmap));
    setPalette(palette);

    QBitmap maskBitMap(":/images/icon_mask");
    setMask(maskBitMap);
}

void AISEntry::mousePressEvent(QMouseEvent *event)
{
    QWidget *activeWnd = QApplication::activeWindow();
    qDebug() << "PRESS " << activeWnd << " THIS " << this << " POS " << focusPolicy();

    if (Qt::LeftButton == event->button())
    {
        dragPosition = event->globalPos() - frameGeometry().topLeft();
        mousePressTimestamp = event->timestamp();
        event->accept();
    }
}

void AISEntry::mouseReleaseEvent(QMouseEvent *event)
{
    QWidget *activeWnd = QApplication::activeWindow();
    qDebug() << "RELEASE " << activeWnd;
    if (Qt::LeftButton == event->button())
    {
        ulong timestampDiff = event->timestamp() - mousePressTimestamp;
        if (timestampDiff > 200)
        {
            return;
        }

        QPoint mousePosition;
        mousePosition = event->globalPos() - frameGeometry().topLeft();
        if (mousePosition.ry() < frameGeometry().height() / 2)
        {
            emit buttonClickedEvent(event);
        }
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

void AISEntry::focusInEvent(QFocusEvent *event)
{
    QWidget *activeWnd = QApplication::activeWindow();
    qDebug() << "focus " << activeWnd << " THIS " << this;
}

void AISEntry::speak()
{
    QMessageBox::information(NULL, tr("test"), tr("Clicked"));
}

void AISEntry::about()
{
    QUrl url("http://www.aiseminar.cn/bbs/forum.php?mod=group&fid=157");
    QDesktopServices::openUrl(url);
}

void AISEntry::focusChanged(QWidget *old, QWidget *now)
{
    QWidget *activeWnd = QApplication::activeWindow();
    qDebug() << "old " << old << " now " << now << " act" << activeWnd;
}
