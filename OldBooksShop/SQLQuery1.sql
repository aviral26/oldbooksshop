USE [OldBooksShop]
GO

DECLARE	@return_value Int

EXEC	@return_value = [dbo].[MAKE_USER_TABLE]

SELECT	@return_value as 'Return Value'

GO
