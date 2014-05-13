#ifndef AISENTRY_H
#define AISENTRY_H

#include <QWidget>

class AISEntry : public QWidget
{
    Q_OBJECT

public:
    AISEntry(QWidget *parent = 0);
    ~AISEntry();

protected:
    void mousePressEvent(QMouseEvent *event);
    void mouseMoveEvent(QMouseEvent *event);

private:
    QPoint dragPosition;
};

#endif // AISENTRY_H
