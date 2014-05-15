#ifndef AISENTRY_H
#define AISENTRY_H

#include <QWidget>

class AISEntry : public QWidget
{
    Q_OBJECT

public:
    AISEntry(QWidget *parent = 0);
    ~AISEntry();

public Q_SLOTS:
    void speak();
    void about();

Q_SIGNALS:
    void buttonClickedEvent(QMouseEvent *event);

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
    HWND concernedWnd;
};

#endif // AISENTRY_H
