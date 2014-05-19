#ifndef AISENTRY_H
#define AISENTRY_H

#include <QWidget>
#include "transceiver.h"

class AISEntry : public QWidget
{
    Q_OBJECT

public:
    AISEntry(QWidget *parent = 0);
    ~AISEntry();

signals:
    void buttonClickedEvent(QMouseEvent *event);

public slots:
    void showServerInfo();
    void speak();
    void about();

protected:
    void mousePressEvent(QMouseEvent *event);
    void mouseReleaseEvent(QMouseEvent *event);
    void mouseMoveEvent(QMouseEvent *event);

private:
    void setWindowShape();
    QString getClipboard();

private:
    QPoint dragPosition;
    ulong mousePressTimestamp;
    HWND concernedWnd; // for record the active window to get selection

    Transceiver myTransceiver;
};

#endif // AISENTRY_H
