package com.nani.engine.game.nonogram;

import com.nani.engine.game.sudoku.Cell;

import java.util.Collection;
import java.util.List;

public class FieldFamily {
    Field[] fields;
    List<Integer> fieldsDesc; // describe how many filled values should be (several tuples, can be zero of them)
    public FieldFamily(Collection<Field> allFields, List<Integer> fieldsDesc) {
        this.fieldsDesc = fieldsDesc;
        int index = 0;
        fields = new Field[allFields.size()];
        for (Field c : allFields) {
            fields[index++] = c;
        }
    }
    public boolean isCompleted() {
        if (!isValid()) return false;
        int fieldsDescNow = 0, lastRowCnt = 0;
        for (Field f : fields) {
            if (f.isFilled()) {
                ++lastRowCnt;
            } else {
                // should be valid here
                if (lastRowCnt > 0) {
                    if (lastRowCnt == fieldsDesc.get(fieldsDescNow)) {
                        ++fieldsDescNow;
                        lastRowCnt = 0;
                    } else {
                        return false;
                    }
                }
            }
        }
        if (lastRowCnt > 0) {
            if (lastRowCnt != fieldsDesc.get(fieldsDescNow))
                return false;
            ++fieldsDescNow;
        }
        return (fieldsDescNow == fieldsDesc.size());
    }
    public boolean isValid() {
        int fieldsDescNow = 0, lastRowCnt  = 0, lastDeletedRowCnt = 0;
        int emptySegment = 0;
        for (Field f : fields) {
            if (f.isFilled()) {
                if (fieldsDesc.size() == 0) return false;
                if (fieldsDescNow == fieldsDesc.size()) {
                    lastRowCnt = lastDeletedRowCnt + emptySegment;
                    --fieldsDescNow;
                }
                ++lastRowCnt;
                emptySegment = 0;
            } else {
                ++emptySegment;
                if (lastRowCnt > 0) {
                    if (fieldsDescNow >= fieldsDesc.size() || lastRowCnt > fieldsDesc.get(fieldsDescNow))
                        return false;
                    ++fieldsDescNow;
                    lastDeletedRowCnt = lastRowCnt;
                    lastRowCnt = 0;
                }
            }
        }
        if (fieldsDescNow > fieldsDesc.size()) return false;
        if (lastRowCnt > 0) {
            if (lastRowCnt > fieldsDesc.get(fieldsDescNow))
                return false;
            ++fieldsDescNow;
        }
        return (fieldsDescNow <= fieldsDesc.size());
    }
    public boolean isStarted() {
        for (Field f : fields)
            if (f.isFilled() || f.isCrossed())
                return true;
        return false;
    }
    public List<Integer> getFieldsDesc() { return fieldsDesc; }

    public String getStringDesc() {
        boolean first = true;
        StringBuilder answer = new StringBuilder();
        for (Integer item : fieldsDesc) {
            if (first) {
                first = false;
            } else {
                answer.append(":");
            }
            answer.append(item);
        }
        answer.append("|");
        return answer.toString();
    }
}
