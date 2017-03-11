package ve.com.abicelis.remindy.database;

/**
 * Created by Alex on 9/3/2017.
 */
public class TableColumn {

    private DataType datatype;
    private String name;

    public TableColumn(DataType datatype, String name) {
        this.datatype = datatype;
        this.name = name;
    }

    public DataType getDataType() {
        return datatype;
    }

    public String getName() {
        return name;
    }

}
