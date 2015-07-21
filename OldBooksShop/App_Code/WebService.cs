using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Data.Sql;
using System.Data.SqlClient;
using System.Configuration;
//using System.Text.StringBuilder;

[WebService(Namespace = "http://tempuri.org/")]
[WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
// To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
// [System.Web.Script.Services.ScriptService]
public class WebService : System.Web.Services.WebService {

    public WebService () {

        //Uncomment the following line if using designed components 
        //InitializeComponent(); 
    }

    
    [WebMethod]
    public string make_user_info_table()
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "Error establishing connection";
            }
             conn.Open();
            //string query = "EXEC MAKE_USER_TABLE";
            SqlCommand cmd = new SqlCommand();
            cmd.Connection = conn;
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.CommandText = "MAKE_USER_TABLE";
            try
            {
                cmd.ExecuteNonQuery();
                conn.Close();
                return "Successfully created user_info table";
            }
            catch (Exception)
            {
                return "Error executing query";
            }
            
        }
        catch (Exception)
        {
           
            return "Error creating user_info table";
        }
    }

    [WebMethod]
    public string make_shopper_info_table()
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
                conn.Open();
            }
            catch (Exception)
            {
                return "Error connecting";
            }
            //string query = "EXEC MAKE_BOOK_TABLE";
            SqlCommand cmd = new SqlCommand();
            cmd.Connection = conn;
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.CommandText = "MAKE_SHOPPER_TABLE";
            cmd.ExecuteNonQuery();
            conn.Close();
            return "Successfully created shopper_info table";

        }
        catch (Exception)
        {
            return "Error creating shopper_info table";
        }
    }

    [WebMethod]
    public string make_books_info_table()
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
                conn.Open();
            }
            catch (Exception)
            {
                return "Error connecting";
            }
            //string query = "EXEC MAKE_BOOK_TABLE";
            SqlCommand cmd = new SqlCommand();
            cmd.Connection = conn;
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.CommandText = "MAKE_BOOK_TABLE";
            cmd.ExecuteNonQuery();
            conn.Close();
            return "Successfully created books_info table";

        }
        catch (Exception)
        {
            return "Error creating user_info table";
        }
    }


    [WebMethod]
    public string make_seller_info_table()
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
                conn.Open();
            }
            catch (Exception)
            {
                return "Error connecting";
            }
            //string query = "EXEC MAKE_SELLER_TABLE";
            SqlCommand cmd = new SqlCommand();
            cmd.Connection = conn;
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.CommandText = "MAKE_SELLER_TABLE";
            cmd.ExecuteNonQuery();
            conn.Close();
            return "Successfully created seller_info table";

        }
        catch (Exception)
        {
            throw;
            //return "Error creating user_info table";
        }
    }


    [WebMethod]
    public string get_user_credentials(string login_id, string password)
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
                conn.Open();
            }
            catch (Exception)
            {
                return "Error with connection";
            }
            
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.Connection = conn;
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.CommandText = "GET_USER_CREDENTIALS";
            cmd.Parameters.AddWithValue("@login_id", login_id);
            cmd.Parameters.AddWithValue("@password", password);
            try
            {
                SqlDataReader result = cmd.ExecuteReader();
                
                if (!result.HasRows)
                {
                    return "no such credentials";
                }
                conn.Close();
                
                return "valid"; //return value in first column of received row.
            }
            catch (Exception e)
            {
                return "Error obtaining result: " + e.Message;
            }
            
        }
        catch (Exception)
        {
            return "Sorry there has been an error.";
            //return "Error creating user_info table";
        }
    }

    [WebMethod]
    public string add_user(string login_id, string password)
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "Error with connection";
            }
            conn.Open();
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Connection = conn;
            cmd.CommandText = "ADD_USER";
            cmd.Parameters.AddWithValue("@login_id", login_id);
            cmd.Parameters.AddWithValue("@password", password);
            cmd.ExecuteNonQuery();

            conn.Close();
            return "Successfully added"; //return value in first column of received row.

        }
        catch (Exception)
        {
            return "Sorry there has been an error.";
            //return "Error creating user_info table";
        }
    }

    [WebMethod]
    public string add_shopper_info(string shopper_id, int book_id, String shopper_phone_number, int quoted_price)
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "Error with connection";
            }
            conn.Open();
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Connection = conn;
            cmd.CommandText = "ADD_SHOPPER_INFO";
            cmd.Parameters.AddWithValue("@shopper_id", shopper_id);
            cmd.Parameters.AddWithValue("@book_id", book_id);
            cmd.Parameters.AddWithValue("@phone_num", Convert.ToInt32(shopper_phone_number));
            cmd.Parameters.AddWithValue("@quoted_price", quoted_price);
            cmd.ExecuteNonQuery();

            conn.Close();
            return "Successfully added"; //return value in first column of received row.

        }
        catch (Exception)
        {
            return "Sorry there has been an error.";
            //return "Error creating user_info table";
        }
    }

    [WebMethod]
    public string add_seller_info(string login_id, int book_id)
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "Error with connection";
            }
            conn.Open();
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Connection = conn;
            cmd.CommandText = "ADD_SELLER";
            cmd.Parameters.AddWithValue("@book_id", book_id);
            cmd.Parameters.AddWithValue("login_id", login_id);
            cmd.ExecuteNonQuery();

            conn.Close();
            return "Successfully added"; //return value in first column of received row.

        }
        catch (Exception)
        {
            return "Sorry there has been an error.";
            //return "Error creating user_info table";
        }
    }

    [WebMethod]
    public string add_book(string book_name, string book_author, int msp, string edition, string publisher, string image, string comments, string category, string login_id)
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "Error with connection";
            }
            conn.Open();
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Connection = conn;
            cmd.CommandText = "ADD_BOOK";
            cmd.Parameters.AddWithValue("@book_name", book_name);
            cmd.Parameters.AddWithValue("@book_author", book_author);
            cmd.Parameters.AddWithValue("@book_msp", msp);
            cmd.Parameters.AddWithValue("@book_Edition", edition);
            cmd.Parameters.AddWithValue("@book_Publisher", publisher);
            cmd.Parameters.AddWithValue("@book_image", image);
            cmd.Parameters.AddWithValue("@book_comments", comments);
            cmd.Parameters.AddWithValue("@book_category", category);
            cmd.Parameters.AddWithValue("@login_id", login_id);
            cmd.ExecuteNonQuery();

            conn.Close();
            return "Successfully added"; //return value in first column of received row.

        }
        catch (Exception e)
        {
            return "Sorry there has been an error." + e.Message;
            //return "Error creating user_info table";
        }
    }

    [WebMethod]
    public string GetMyBooks(string login_id)
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "Error with connection"; 
            }
            conn.Open();
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Connection = conn;
            cmd.CommandText = "GET_MY_BOOKS";
            cmd.Parameters.AddWithValue("@login_id", login_id);
            SqlDataReader dr = cmd.ExecuteReader();
            System.Text.StringBuilder result = new System.Text.StringBuilder();

            while (dr.HasRows)
            {
                while (dr.Read())
                {

                    String temp = dr.GetString(dr.GetOrdinal("Book_name"));
                    result.Append(temp);
                    result.Append("`");
                    temp = dr.GetString(dr.GetOrdinal("Book_author"));
                    result.Append(temp);
                    result.Append("`");
                    temp = dr.GetDouble(dr.GetOrdinal("Book_MSP")).ToString();
                    result.Append(temp);
                    result.Append("`");
                    temp = dr.GetDouble(dr.GetOrdinal("Book_Edition")).ToString();
                    result.Append(temp);
                    result.Append("`");
                    temp = dr.GetString(dr.GetOrdinal("Book_Publisher"));
                    result.Append(temp);
                    result.Append("`");
                    temp = dr.GetInt32(dr.GetOrdinal("Book_id")).ToString();
                    result.Append(temp);
                    result.Append("`");
                    temp = dr.GetString(dr.GetOrdinal("Book_image"));
                    result.Append(temp);
                    result.Append("`");
                    temp = dr.GetDateTime(dr.GetOrdinal("Book_validTill")).ToString();
                    result.Append(temp);
                    result.Append(";");
                 
                }
                dr.NextResult();
            }
            conn.Close();
            return result.ToString();
        }
        catch (Exception e)
        { 
            //e.StackTrace;
            return "GetMyBooksError: " + e.Message;
            //return "Error creating user_info table";
        }
    }

    [WebMethod]
    public string GetSearchResult(string keyword,int page)
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "Error with connection";
            }
            conn.Open();
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Connection = conn;
            cmd.CommandText = "GET_SEARCH_RESULT";
            cmd.Parameters.AddWithValue("@keyword", keyword);
            SqlDataReader dr = cmd.ExecuteReader();
            System.Text.StringBuilder result = new System.Text.StringBuilder();

            while (dr.HasRows)
            {
                while (dr.Read())
                {

                    int id = dr.GetInt32(dr.GetOrdinal("Book_id"));
                    result.Append(id.ToString());
                    result.Append("`");
                    result.Append(dr.GetString(dr.GetOrdinal("Book_name")));
                    result.Append("`");
                    result.Append(dr.GetString(dr.GetOrdinal("Book_author")));
                    result.Append("`");
                    result.Append(dr.GetDouble(dr.GetOrdinal("Book_MSP")).ToString());
                    result.Append("`");
                    result.Append(dr.GetDouble(dr.GetOrdinal("Book_Edition"))).ToString();
                    result.Append("`");
                    result.Append(dr.GetString(dr.GetOrdinal("Book_Publisher")));
                    result.Append("`");
                    if (!dr.GetString(dr.GetOrdinal("Book_image")).Equals(null))
                    {
                        result.Append(dr.GetString(dr.GetOrdinal("Book_image")));
                        result.Append("`");

                    }
                    else
                    {
                        result.Append("Nothing here");
                        result.Append("`");
                    }
                    result.Append(dr.GetString(dr.GetOrdinal("Book_Comments")));
                    result.Append("|");
                }
                dr.NextResult();

            }
            conn.Close();
            String[] temp = result.ToString().Split('|');
            System.Text.StringBuilder new_result = new System.Text.StringBuilder(null);
            int term = page + 20;
            
            if (term >= temp.Length)
            {
                term = temp.Length;
            }

            if (page >= temp.Length)
            {
                new_result = null;
                term = page;
            }
            //new_result.Append(page).Append(term);

            for (int i = page; i < term; i++)
            {
                new_result.Append(temp[i]);
                new_result.Append("|");
            }
            return new_result.ToString();
        }
        catch (Exception e)
        {
            //e.StackTrace;
            return "GetSearchResultError: " + e.Message;
            //return "Error creating user_info table";
        }
    }

    [WebMethod]
    public string removeMyBook(int book_id, string login_id)
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "false";
            }
            conn.Open();
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Connection = conn;
            cmd.CommandText = "REMOVE_MY_BOOK";
            cmd.Parameters.AddWithValue("@book_id", book_id);
            cmd.Parameters.AddWithValue("@login_id", login_id);
            cmd.ExecuteNonQuery();
            conn.Close();
            return "true";
        }
        catch (Exception)
        {
            return "false";
            //return "Error creating user_info table";
        }
    }


    [WebMethod]
    public string GetShoppersByBook(int book_id,string login_id)
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "GetShoppersByBookError";
            }
            conn.Open();
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Connection = conn;
            cmd.CommandText = "GET_SHOPPERS_BY_BOOK";
            cmd.Parameters.AddWithValue("@book_id", book_id);
            cmd.Parameters.AddWithValue("@login_id", login_id);
            SqlDataReader dr = cmd.ExecuteReader();
            System.Text.StringBuilder result = new System.Text.StringBuilder();

            while(dr.HasRows)
            {
                 while (dr.Read())
                {
                    result.Append(dr.GetString(dr.GetOrdinal("shopper_id")));
                    result.Append("`");
                    result.Append(dr.GetInt32(dr.GetOrdinal("quoted_price")).ToString());
                    result.Append("`");
                    try
                    {
                        result.Append(dr.GetString(dr.GetOrdinal("shopper_phone_number")));
                    }
                    catch (Exception){
                        result.Append("0000");
                    }
                    result.Append(";");
                    
                }
                 dr.NextResult();  
            }

            conn.Close();
            return result.ToString();
        }
        catch (Exception)
        {
            return "GetShoppersByBookError";
            //return "Error creating user_info table";
        }
    }

    [WebMethod]
    public string GetBooksInfoByCategory(string category, int page)
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "Error with connection";
            }
            conn.Open();
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Connection = conn;
            cmd.CommandText = "GET_BOOKSINFO_BY_CATEGORY";
            cmd.Parameters.AddWithValue("@category", category);
            SqlDataReader dr = cmd.ExecuteReader();
            System.Text.StringBuilder result = new System.Text.StringBuilder();

            while(dr.HasRows)
            {
                while (dr.Read())
                {

                    int id = dr.GetInt32(dr.GetOrdinal("Book_id"));
                    result.Append(id.ToString());
                    result.Append("`");
                    result.Append(dr.GetString(dr.GetOrdinal("Book_name")));
                    result.Append("`");
                    result.Append(dr.GetString(dr.GetOrdinal("Book_author")));
                    result.Append("`");
                    result.Append(dr.GetDouble(dr.GetOrdinal("Book_MSP")).ToString());
                    result.Append("`");
                    result.Append(dr.GetDouble(dr.GetOrdinal("Book_Edition"))).ToString();
                    result.Append("`");
                    result.Append(dr.GetString(dr.GetOrdinal("Book_Publisher")));
                    result.Append("`");
                    if (!dr.GetString(dr.GetOrdinal("Book_image")).Equals(null))
                    {
                        result.Append(dr.GetString(dr.GetOrdinal("Book_image")));
                        result.Append("`");
                    
                    }
                    else
                    {
                        result.Append("Nothing here");
                        result.Append("`");
                    }
                    result.Append(dr.GetString(dr.GetOrdinal("Book_Comments")));
                    result.Append("|");
                }
                dr.NextResult();

            }
            conn.Close();
            String[] temp = result.ToString().Split('|');
            System.Text.StringBuilder new_result = new System.Text.StringBuilder();

            int term = page + 20;
            if (page + 20 >= temp.Length)
            {
                term = temp.Length;
            }
            
            if (page >= temp.Length)
            {
                new_result = null;
                term = page;
            }
            
            for (int i = page; i < term; i++)
            {
                new_result.Append(temp[i]);
                new_result.Append("|");
            }

            return new_result.ToString();
       }
        catch (Exception e)
        {
            //return "GetBooksInfoByCategoryError" ;
            return "Sorry there has been an error: " + e.Message;
            //return "Error creating user_info table";
        }
    }

    [WebMethod]
    public string GetCategories()
    {
        try
        {
            SqlConnection conn;
            try
            {
                conn = new SqlConnection("Data Source=162.222.225.88; Initial Catalog=quality_test;User ID=quality_test;Password=wipro12#;");
            }
            catch (Exception)
            {
                return "Error with connection";
            }
            conn.Open();
            //string query = "EXEC GET_USER_CREDENTIALS @login_id=" +login_id+",@password=" +password;
            SqlCommand cmd = new SqlCommand();
            cmd.CommandType = System.Data.CommandType.StoredProcedure;
            cmd.Connection = conn;
            cmd.CommandText = "GET_CATEGORIES";
            //cmd.Parameters.AddWithValue("@category", category);
            SqlDataReader dr = cmd.ExecuteReader();
            System.Text.StringBuilder result = new System.Text.StringBuilder();

            while (dr.HasRows)
            {
                while (dr.Read())
                {

                    //int id = dr.GetInt32(dr.GetOrdinal("Book_id"));
                    //result.Append(id.ToString());
                    //result.Append("`");
                    result.Append(dr.GetString(dr.GetOrdinal("Book_Category")));
                    result.Append("`");
                    //result.Append(dr.GetString(dr.GetOrdinal("Book_author")));
                    //result.Append("`");
                    //result.Append(dr.GetInt32(dr.GetOrdinal("Book_MSP")).ToString());
                    //result.Append("`");
                    //result.Append(dr.GetString(dr.GetOrdinal("Book_Edition")));
                    //result.Append("`");
                    //result.Append(dr.GetString(dr.GetOrdinal("Book_Publisher")));
                    //result.Append("`");
                    //result.Append(dr.GetString(dr.GetOrdinal("Book_image")));
                    //result.Append("`");
                    //result.Append(dr.GetString(dr.GetOrdinal("Book_Comments")));
                    //result.Append("|");
                }
                dr.NextResult();

            }
            conn.Close();
            return result.ToString();
        }
        catch (Exception e)
        {
            return "GetCategoriesError";
            //return "Sorry there has been an error: " + e.Message;
            //return "Error creating user_info table";
        }
    }
}
