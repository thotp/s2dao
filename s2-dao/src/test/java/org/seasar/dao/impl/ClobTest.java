package org.seasar.dao.impl;

import java.io.Serializable;
import java.sql.DatabaseMetaData;

import org.seasar.extension.jdbc.types.StringClobType;
import org.seasar.extension.unit.S2TestCase;

public class ClobTest extends S2TestCase {

    private LargeTextDao largeTextDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("ClobTest.dicon");
    }

    public void test1Tx() throws Exception {
        assertNotNull(largeTextDao);
        final DatabaseMetaData metaData = getConnection().getMetaData();
        System.out.println(metaData.getDatabaseProductName());
        System.out.println(metaData.getDatabaseProductVersion());
        final LargeText largeText = largeTextDao.getLargeText(123);
        assertEquals(null, largeText);
    }

    public void test2Tx() throws Exception {
        {
            LargeText largeText = new LargeText();
            largeText.setId(1);
            largeText.setLargeString("abc1");
            largeTextDao.insert(largeText);
        }
        {
            final LargeText largeText = largeTextDao.getLargeText(1);
            assertEquals("abc1", largeText.getLargeString());
            assertEquals(0, largeText.getVersionNo());

            largeText.setLargeString("ABCDEFG");
            largeTextDao.update(largeText);
        }
        {
            final LargeText largeText = largeTextDao.getLargeText(1);
            assertEquals("ABCDEFG", largeText.getLargeString());
            assertEquals(1, largeText.getVersionNo());
        }
    }

    public static interface LargeTextDao {

        public Class BEAN = LargeText.class;

        public String getLargeText_ARGS = "id";

        public LargeText getLargeText(int id);

        public void insert(LargeText largeText);

        public void update(LargeText largeText);

    }

    public static class LargeText implements Serializable {

        private static final long serialVersionUID = 1L;

        public static final String TABLE = "LARGE_TEXT";

        private int id;

        public static Class largeString_VALUE_TYPE = StringClobType.class;

        private String largeString;

        private int versionNo;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLargeString() {
            return largeString;
        }

        public void setLargeString(String largeString) {
            this.largeString = largeString;
        }

        public int getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(int versionNo) {
            this.versionNo = versionNo;
        }
    }

}
