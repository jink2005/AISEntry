#include "aisentry.h"
#include <QtWidgets>
#include <qt_windows.h>

AISEntry::AISEntry(QWidget *parent)
    : QWidget(parent, Qt::FramelessWindowHint | Qt::WindowStaysOnTopHint | Qt::WindowSystemMenuHint)
{
    /**
     * Set backgroud and window shape
     */
    setWindowShape();

    QPixmap pixmap(":/images/icon");
    setWindowIcon(QIcon(pixmap));

    setMouseTracking(true);

    /**
     * set context menu
     */

    QAction *showServerInfoAction = new QAction(tr("Server &Info"), this);
    connect(showServerInfoAction, SIGNAL(triggered()), this, SLOT(showServerInfo()));
    addAction(showServerInfoAction);

//    QAction *speakAction = new QAction(tr("&Speak"), this);
//    connect(speakAction, SIGNAL(triggered()), this, SLOT(speak()));
//    addAction(speakAction);
    connect(this, SIGNAL(buttonClickedEvent(QMouseEvent*)), SLOT(speak()));

    QAction *aboutAction = new QAction(tr("&About"), this);
    connect(aboutAction, SIGNAL(triggered()), this, SLOT(about()));
    addAction(aboutAction);

    QAction *quitAction = new QAction(tr("E&xit"), this);
    connect(quitAction, SIGNAL(triggered()), qApp, SLOT(quit()));
    addAction(quitAction);

    setContextMenuPolicy(Qt::ActionsContextMenu);
    setWindowTitle(tr("AISEntry"));

    /* process with transceiver */
    connect(&myTransceiver, SIGNAL(newMessage(QString,QString)), this, SLOT(incomeMessage(QString,QString)));
    connect(&myTransceiver, SIGNAL(newParticipant(QString)), this, SLOT(newConnection(QString)));
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
    if (Qt::LeftButton == event->button())
    {
        dragPosition = event->globalPos() - frameGeometry().topLeft();
        mousePressTimestamp = event->timestamp();
        event->accept();
    }
}

void AISEntry::mouseReleaseEvent(QMouseEvent *event)
{
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
            concernedWnd = GetForegroundWindow(); // Record the current focus window
        }
        else
        {
            setCursor(Qt::ArrowCursor);
        }
    }
}

void AISEntry::showServerInfo()
{
    QToolTip::showText(frameGeometry().topRight(), myTransceiver.getServerInfo(), 0, QRect(), 30000);
//    QMessageBox::information(NULL, tr("Server Info"), myTransceiver.getServerInfo());
}

void AISEntry::incomeMessage(const QString &from, const QString &message)
{
    if (from.isEmpty() || message.isEmpty())
        return;

    QString text = QToolTip::text();
    if (! text.isEmpty())
        text.append("\n");

    QToolTip::showText(frameGeometry().topRight(), text + from + ":\n" + message);
}

void AISEntry::newConnection(const QString &partner)
{
    if (partner.isEmpty())
        return;

    QString text = QToolTip::text();
    if (! text.isEmpty())
        text.append("\n");

    QToolTip::showText(frameGeometry().topRight(), text + partner + " has connected!");
}

void AISEntry::speak()
{
    if (NULL != concernedWnd && (HWND)this->winId() != concernedWnd)
    {
        SetForegroundWindow(concernedWnd);

        keybd_event(VK_CONTROL, MapVirtualKey(VK_CONTROL, 0), 0, 0);
        keybd_event('C', MapVirtualKey('C', 0), 0, 0);
        keybd_event('C', MapVirtualKey('C', 0), KEYEVENTF_KEYUP, 0);
        keybd_event(VK_CONTROL, MapVirtualKey(VK_CONTROL, 0), KEYEVENTF_KEYUP, 0);

        QThread::sleep(1); // waiting 1s for the clipcoard data to change
        QString data = getClipboard();
        if (! data.isEmpty())
        {
            myTransceiver.sendMessage(data);
        }
    }
}

QString AISEntry::getClipboard()
{
    QClipboard *clipboard = QApplication::clipboard();
    return clipboard->text();
}

void AISEntry::about()
{
    QUrl url("http://www.aiseminar.cn/bbs/forum.php?mod=group&fid=157");
    QDesktopServices::openUrl(url);
}
