package classes;

import android.content.Context;
import android.util.Log;

import com.shoppinggai.gridpos.gridposclient.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;

/**
 * Created by admin0 on 4/24/16.
 */
public class FakeDataManager {
    private Context context;
    private NumberFormat moneyFormat;

    public FakeDataManager(Context current) {

        this.context = current;

        moneyFormat = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setCurrencySymbol("$");
        ((DecimalFormat) moneyFormat).setDecimalFormatSymbols(symbols);
    }

    public JSONArray readJsonArray(String filename) throws IOException, JSONException {

        String json;

        try {

            int resID = context.getResources().getIdentifier(filename, "raw", context.getPackageName());

            InputStream is = context.getResources().openRawResource(resID);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

            return new JSONArray(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject findUserByEmail(String email) throws IOException, JSONException {
        JSONArray fakeDataArray = this.readJsonArray("users");
        if (fakeDataArray == null) {
            return null;
        }
        for (int i = 0; i < fakeDataArray.length(); i++) {
            JSONObject row = fakeDataArray.getJSONObject(i);
            if (row.getString("email").equals(email))
                return row;
        }
        return null;
    }

    public JSONArray findGridsByUserId(int id) throws IOException, JSONException {

        JSONArray allGrids = this.readJsonArray("grids");
        JSONArray userGrids = new JSONArray();

        if (allGrids == null) {
            return null;
        }

        for (int i = 0; i < allGrids.length(); i++) {

            JSONObject row = allGrids.getJSONObject(i);

            if (row.getInt("user_id") == id) {

                JSONArray products = this.findProductsByGridId(row.getInt("id"));

                row.put("num_products", products.length());

                userGrids.put(row);
            }
        }

        return userGrids;
    }

    public JSONArray findSalesByProductId(int id) throws IOException, JSONException {

        JSONArray allSales = this.readJsonArray("sales");
        JSONArray productSales = new JSONArray();

        if (allSales == null) {
            return null;
        }

        for (int i = 0; i < allSales.length(); i++) {

            JSONObject row = allSales.getJSONObject(i);

            if (row.getInt("product_id") == id) {
                productSales.put(row);
            }
        }

        return productSales;
    }

    // Many to one (many: sales, one: invoice)
    public JSONObject findInvoiceBySalesId(int id) throws IOException, JSONException {

        JSONArray allInvoices = this.readJsonArray("invoices");

        if (allInvoices == null) {
            return null;
        }

        for (int i = 0; i < allInvoices.length(); i++) {

            JSONObject row = allInvoices.getJSONObject(i);

            if (row.getInt("id") == id) {
                // Return only one record because only one salesId is provided
                return row;
            }
        }
        return null;
    }

    public JSONArray findInvoicesByProductId(int id) throws IOException, JSONException {

        JSONArray productSales = this.findSalesByProductId(id);
        JSONArray productInvoices = new JSONArray();

        for (int i = 0; i < productSales.length(); i++) {
            JSONObject record = productSales.getJSONObject(i);
            JSONObject invoice = this.findInvoiceBySalesId(record.getInt("invoice_id"));
            if (invoice != null) {
                invoice.put("qty", record.getInt("qty"));
                productInvoices.put(invoice);
            }
        }

        return productInvoices;
    }

    public JSONArray findSalesByInvoiceId(int id) throws IOException, JSONException {

        JSONArray allSales = this.readJsonArray("sales");
        JSONArray invoiceSales = new JSONArray();

        if (allSales == null) {
            return null;
        }

        for (int i = 0; i < allSales.length(); i++) {

            JSONObject sale = allSales.getJSONObject(i);

            if (sale.getInt("invoice_id") == id) {
                invoiceSales.put(sale);
            }
        }
        return invoiceSales;
    }

    public Double calcInvoiceAmount(int id) throws IOException, JSONException {
        Double amount = 0.0;
        JSONArray sales = this.findSalesByInvoiceId(id);
        for (int i = 0; i < sales.length(); i++) {
            JSONObject sale = sales.getJSONObject(i);
            JSONObject product = this.findProductById(sale.getInt("product_id"));
            amount += sale.getInt("qty") * product.getDouble("price");
        }
        return amount;
    }

    public JSONArray findInvoicesByGridId(int id) throws IOException, JSONException {

        JSONArray allInvoices = this.readJsonArray("invoices");
        JSONArray gridInvoices = new JSONArray();

        if (allInvoices == null) {
            return null;
        }

        for (int i = 0; i < allInvoices.length(); i++) {

            JSONObject invoice = allInvoices.getJSONObject(i);

            if (invoice.getInt("grid_id") == id) {
                invoice.put("amount", calcInvoiceAmount(invoice.getInt("id")));
                gridInvoices.put(invoice);
            }
        }
        return gridInvoices;
    }

    public JSONArray findDateAndQtyByProductId(int id) throws IOException, JSONException {

        JSONArray records = new JSONArray();
        JSONArray invoices = this.findInvoicesByProductId(id);

        JSONObject record;
        JSONObject invoice;

        int j = 0;
        String _date = "";
        for (int i = 0; i < invoices.length(); i++) {
            invoice = invoices.getJSONObject(i);
            String date = invoice.getString("date");
            int qty = invoice.getInt("qty");
            Log.d("invoice-id", String.valueOf(invoice.getInt("id")));
            Log.d("date", date);
            Log.d("qty", String.valueOf(qty));
            Log.d("j", String.valueOf(j));
            if (!date.equals(_date)) {
                record = new JSONObject();
                record.put("date", date);
                record.put("qty", qty);
                records.put(record);
                _date = date;
                if (i > 0)
                    j++;
            }
            else {
                record = records.getJSONObject(j);
                int newQty = record.getInt("qty") + qty;

                record.put("qty", newQty);
            }
        }

        return records;
    }

    public JSONArray findProductsByUserId(int id) throws IOException, JSONException {
        JSONArray fakeDataArray = this.readJsonArray("products");
        JSONArray grids = new JSONArray();
        if (fakeDataArray == null) {
            return null;
        }
        for (int i = 0; i < fakeDataArray.length(); i++) {
            JSONObject row = fakeDataArray.getJSONObject(i);
            if (row.getInt("user_id") == id)
                grids.put(row);
        }
        return grids;
    }

    public JSONArray findProductsByGridId(int id) throws IOException, JSONException {

        JSONArray allProducts = this.readJsonArray("products");
        JSONArray gridProducts = new JSONArray();

        if (allProducts == null) {
            return null;
        }

        for (int i = 0; i < allProducts.length(); i++) {

            JSONObject row = allProducts.getJSONObject(i);

            if (row.getInt("grid_id") == id)
                gridProducts.put(row);
        }

        return gridProducts;
    }

    public JSONObject findGridById(int id) throws IOException, JSONException {

        JSONArray allGrids = this.readJsonArray("grids");

        if (allGrids == null) {
            return null;
        }

        for (int i = 0; i < allGrids.length(); i++) {

            JSONObject row = allGrids.getJSONObject(i);

            if (row.getInt("id") == id) {
                return row;
            }
        }

        return null;
    }

    public JSONObject findProductById(int id) throws IOException, JSONException {

        JSONArray allProducts = this.readJsonArray("products");

        if (allProducts == null) {
            return null;
        }

        for (int i = 0; i < allProducts.length(); i++) {

            JSONObject row = allProducts.getJSONObject(i);

            if (row.getInt("id") == id) {
                return row;
            }
        }

        return null;
    }

    public JSONObject findInvoiceById(int id) throws IOException, JSONException {

        JSONArray allInvoices = this.readJsonArray("invoices");

        if (allInvoices == null) {
            return null;
        }

        for (int i = 0; i < allInvoices.length(); i++) {

            JSONObject row = allInvoices.getJSONObject(i);

            if (row.getInt("id") == id) {
                return row;
            }
        }

        return null;
    }

    public JSONObject getInvoiceDetails(int id) throws IOException, JSONException {

        JSONObject details = new JSONObject();
        JSONArray salesRecords = new JSONArray();

        JSONObject invoice = this.findInvoiceById(id);
        JSONArray sales = this.findSalesByInvoiceId(id);

        Double totalAmount = 0.0;

        for (int i = 0; i < sales.length(); i++) {

            JSONObject row = new JSONObject();
            JSONObject sale = sales.getJSONObject(i);
            JSONObject product = this.findProductById(sale.getInt("product_id"));

//            row.put("sale_qty", sale.getInt("qty"));
//            row.put("product_name", product.getString("name"));
//            row.put("product_price", product.getDouble("price"));
//            row.put("amount", sale.getInt("qty") * product.getDouble("price"));

            row.put("col_1", product.getString("name"));
            row.put("col_2", moneyFormat.format(product.getDouble("price")));
            row.put("col_3", sale.getInt("qty"));
            row.put("col_4", moneyFormat.format(sale.getInt("qty") * product.getDouble("price")));

            salesRecords.put(row);

            totalAmount += sale.getInt("qty") * product.getDouble("price");
        }

        details.put("sales_records", salesRecords);
        details.put("total_amount", totalAmount);
        details.put("invoice_ref", invoice.getString("ref"));
        details.put("invoice_date", invoice.getString("date"));
        details.put("invoice_time", invoice.getString("time"));

//        Log.d("details", String.valueOf(details));

        return details;
    }

}
