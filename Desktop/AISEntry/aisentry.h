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
    void focusChanged(QWidget *old, QWidget *now);

Q_SIGNALS:
    void buttonClickedEvent(QMouseEvent *event);

protected:
    void mousePressEvent(QMouseEvent *event);
    void mouseReleaseEvent(QMouseEvent *event);
    void mouseMoveEvent(QMouseEvent *event);
    void focusInEvent(QFocusEvent *event);

private:
    void setWindowShape();

private:
    QPoint dragPosition;
    ulong mousePressTimestamp;
};

#endif // AISENTRY_H
