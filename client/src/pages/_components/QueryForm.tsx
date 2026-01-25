interface Props {
  isPending: boolean;
  payload: (payload: FormData) => void;
}

export const QueryForm = ({ isPending, payload }: Props) => {
  return (
    <>
      <form action={payload} className="flex justify-center m-4">
        <input
          type="number"
          name="offset"
          placeholder="Offset"
          className="input validator"
          min="0"
          max="1000000"
          required
        />
        <input
          type="number"
          name="limit"
          placeholder="Limit"
          className="input validator"
          min="1"
          max="1000"
          required
        />
        <input type="submit" disabled={isPending} />
      </form>
      <div className="divider"></div>
    </>
  );
};
